package dev.breezes.settlements.domain.generation.layout;

import dev.breezes.settlements.domain.generation.model.building.BuildingAssignment;
import dev.breezes.settlements.domain.generation.model.geometry.BlockPosition;
import dev.breezes.settlements.domain.generation.model.geometry.BoundingRegion;
import dev.breezes.settlements.domain.generation.model.layout.RoadSegment;
import dev.breezes.settlements.domain.generation.model.layout.RoadType;
import dev.breezes.settlements.domain.generation.model.survey.TerrainGrid;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class RoadNetworkGenerator {

    /**
     * Builds the settlement road network using Prim's MST over the nuclei node set, then
     * adds up to 2 short-circuit edges to create loops, and displaces each edge into an
     * organic curve via {@link #segmentEdge}.
     * <p>
     * Algorithm outline:
     * <ol>
     *   <li>Collect unique XZ nodes: the planning center plus the center of each nucleus plot.</li>
     *   <li>Run Prim's MST (greedy, weight = squared XZ distance) seeded from the planning center
     *       to guarantee the planning center is connected to every nucleus.</li>
     *   <li>Select 0–2 extra non-MST edges whose Euclidean length is ≤ 2× the longest MST edge
     *       (weight ≤ 4× longest MST weight, since weights are distance-squared),
     *       preferring shorter ones, to introduce loops without sprawling side roads.</li>
     *   <li>Classify edges: MST edges touching the planning center become {@code MAIN} roads;
     *       all other MST edges become {@code SECONDARY}; extras become {@code SIDE}.</li>
     *   <li>Emit each edge as 1–4 {@link RoadSegment}s via midpoint displacement, then
     *       register their AABB bounds with the placement grid as road-occupied regions.</li>
     * </ol>
     */
    public List<RoadSegment> generateRoads(BlockPosition planningCenter,
                                           List<BuildingAssignment> nuclei,
                                           TerrainGrid terrainGrid,
                                           PlacementGrid grid,
                                           Random random) {
        List<BlockPosition> nodes = new ArrayList<>();
        nodes.add(planningCenter);
        for (BuildingAssignment nucleus : nuclei) {
            BlockPosition center = nucleus.plot().bounds().centerXZ().withY(nucleus.plot().targetY());
            if (!center.sameXZ(planningCenter) && nodes.stream().noneMatch(center::sameXZ)) {
                nodes.add(center);
            }
        }
        if (nodes.size() <= 1) {
            return List.of();
        }

        List<Edge> allEdges = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = i + 1; j < nodes.size(); j++) {
                allEdges.add(new Edge(nodes.get(i), nodes.get(j), LayoutSupport.distanceSquaredXZ(nodes.get(i), nodes.get(j))));
            }
        }

        List<Edge> mst = new ArrayList<>();
        Set<BlockPosition> visited = new HashSet<>();
        visited.add(planningCenter);
        while (visited.size() < nodes.size()) {
            Edge next = allEdges.stream()
                    .filter(edge -> visited.contains(edge.a()) ^ visited.contains(edge.b()))
                    .min(Comparator.comparingLong(Edge::weight))
                    .orElse(null);
            if (next == null) {
                break;
            }
            mst.add(next);
            visited.add(next.a());
            visited.add(next.b());
        }

        long maxMstWeight = mst.stream().mapToLong(Edge::weight).max().orElse(0L);
        // Edge weights are distance-squared, so "2× the longest MST distance" becomes
        // (2 * distance)² = 4 * distanceSquared.  The factor 4L is intentional.
        List<Edge> extras = allEdges.stream()
                .filter(edge -> !mst.contains(edge))
                .filter(edge -> edge.weight() <= maxMstWeight * 4L)
                .sorted(Comparator.comparingLong(Edge::weight))
                .limit(random.nextInt(0, 3))
                .toList();

        List<RoadSegment> roads = new ArrayList<>();
        for (Edge edge : mst) {
            RoadType type = planningCenter.sameXZ(edge.a()) || planningCenter.sameXZ(edge.b()) ? RoadType.MAIN : RoadType.SECONDARY;
            roads.addAll(this.segmentEdge(edge.a(), edge.b(), type, terrainGrid, random, true));
        }
        for (Edge edge : extras) {
            roads.addAll(this.segmentEdge(edge.a(), edge.b(), RoadType.SIDE, terrainGrid, random, true));
        }

        for (RoadSegment road : roads) {
            for (BoundingRegion region : LayoutSupport.rasterizeRoad(road)) {
                grid.occupyRoad(region);
            }
        }
        return List.copyOf(roads);
    }

    /**
     * Converts a straight edge between two nodes into 1–4 organic {@link RoadSegment}s via
     * midpoint displacement.
     * <p>
     * A midpoint is computed at the center of the edge, then displaced perpendicularly by a
     * random offset. The offset magnitude scales with edge length ({@code length / 80}) so
     * longer edges curve more but remain proportionally plausible. The displaced midpoint's
     * Y coordinate is sampled from the terrain grid.
     * <p>
     * Recursion: if {@code allowRecursion} is {@code true} and the edge is longer than
     * 40 blocks, the two halves ({@code start → displaced} and {@code displaced → end}) are
     * each segmented again, producing a maximum of 4 segments per original edge.
     * This recursion happens only ONCE.
     * <p>
     * Edges shorter than 8 blocks are emitted as a single straight segment.
     */
    private List<RoadSegment> segmentEdge(BlockPosition start,
                                          BlockPosition end,
                                          RoadType type,
                                          TerrainGrid terrainGrid,
                                          Random random,
                                          boolean allowRecursion) {
        double length = LayoutSupport.segmentLengthXZ(start, end);
        if (length < 8.0d) {
            return List.of(new RoadSegment(start, end, type));
        }

        double midRatio = 0.5d;
        BlockPosition midpoint = LayoutSupport.interpolateOnSegment(start, end, midRatio, terrainGrid);
        double dx = end.x() - start.x();
        double dz = end.z() - start.z();
        double magnitude = Math.max(1.0d, Math.sqrt(dx * dx + dz * dz));
        double perpX = -dz / magnitude;
        double perpZ = dx / magnitude;
        double offsetScale = Math.max(1.0d, length / 80.0d);
        int offset = (int) Math.round(random.nextInt(-8, 9) * offsetScale);
        int displacedX = midpoint.x() + (int) Math.round(perpX * offset);
        int displacedZ = midpoint.z() + (int) Math.round(perpZ * offset);
        BlockPosition displacedMid = new BlockPosition(displacedX, terrainGrid.getHeightAtWorld(displacedX, displacedZ), displacedZ);

        if (allowRecursion && length > 40.0d) {
            List<RoadSegment> segments = new ArrayList<>();
            segments.addAll(this.segmentEdge(start, displacedMid, type, terrainGrid, random, false));
            segments.addAll(this.segmentEdge(displacedMid, end, type, terrainGrid, random, false));
            return segments;
        }

        return List.of(
                new RoadSegment(start, displacedMid, type),
                new RoadSegment(displacedMid, end, type)
        );
    }

    private record Edge(BlockPosition a, BlockPosition b, long weight) {
    }

}
