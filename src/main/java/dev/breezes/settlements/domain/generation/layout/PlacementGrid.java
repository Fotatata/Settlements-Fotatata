package dev.breezes.settlements.domain.generation.layout;

import dev.breezes.settlements.domain.generation.model.geometry.BoundingRegion;

import java.util.ArrayList;
import java.util.List;

public class PlacementGrid {

    public static final int DEFAULT_MINIMUM_SPACING = 4;

    private final List<OccupiedRegion> occupiedRegions;
    private final int minimumSpacing;

    public PlacementGrid() {
        this(DEFAULT_MINIMUM_SPACING);
    }

    public PlacementGrid(int minimumSpacing) {
        if (minimumSpacing < 0) {
            throw new IllegalArgumentException("minimumSpacing must be >= 0");
        }
        this.minimumSpacing = minimumSpacing;
        this.occupiedRegions = new ArrayList<>();
    }

    public void occupy(BoundingRegion bounds) {
        this.occupiedRegions.add(new OccupiedRegion(bounds, false));
    }

    public void occupyRoad(BoundingRegion bounds) {
        this.occupiedRegions.add(new OccupiedRegion(bounds, true));
    }

    /**
     * Returns {@code true} if placing a building with the given bounds would conflict with
     * any previously occupied region.
     * <p>
     * Two different spacing rules are applied depending on whether the occupied region is a
     * road or a building:
     * <ul>
     *   <li><b>Road regions</b> -- checked with direct intersection only. Roads act as the
     *       inter-plot padding themselves, so no additional spacing buffer is applied. A
     *       building may sit right up against a road.</li>
     *   <li><b>Building regions</b> -- the candidate is expanded by {@link #minimumSpacing}
     *       before the intersection test, enforcing a minimum gap between any two
     *       buildings.</li>
     * </ul>
     */
    public boolean hasConflict(BoundingRegion candidate) {
        BoundingRegion expanded = candidate.expandedBy(this.minimumSpacing);
        for (OccupiedRegion occupied : this.occupiedRegions) {
            // Check intersection with roads
            if (occupied.road()) {
                if (candidate.intersects(occupied.bounds())) {
                    return true;
                }
                continue;
            }

            // Check intersection with other buildings
            if (expanded.intersects(occupied.bounds())) {
                return true;
            }
        }
        return false;
    }

    public int minimumSpacing() {
        return this.minimumSpacing;
    }

    public List<BoundingRegion> occupiedRegions() {
        return this.occupiedRegions.stream().map(OccupiedRegion::bounds).toList();
    }

    private record OccupiedRegion(BoundingRegion bounds, boolean road) {
    }

}
