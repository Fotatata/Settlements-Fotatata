package dev.breezes.settlements.domain.generation.survey;

import dev.breezes.settlements.domain.common.BiomeId;
import dev.breezes.settlements.domain.generation.model.geometry.BlockPosition;
import dev.breezes.settlements.domain.generation.model.survey.ElevationStats;
import dev.breezes.settlements.domain.generation.model.survey.ResourceTag;
import dev.breezes.settlements.domain.generation.model.survey.SiteReport;
import dev.breezes.settlements.domain.generation.model.survey.SurveyBounds;
import dev.breezes.settlements.domain.generation.model.survey.TerrainGrid;
import dev.breezes.settlements.domain.generation.model.survey.TerrainSample;
import dev.breezes.settlements.domain.generation.model.survey.WaterFeatureType;
import lombok.AllArgsConstructor;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@AllArgsConstructor
public class SurveyEngine {

    private final BiomeSurveyLookup biomeLookup;

    public SiteReport analyze(@Nonnull TerrainGrid grid, @Nonnull SurveyBounds bounds) {
        int gridWidth = grid.gridWidth();
        int gridDepth = grid.gridDepth();
        int gridSize = gridWidth * gridDepth;

        int totalSamples = 0;
        int heightMin = Integer.MAX_VALUE;
        int heightMax = Integer.MIN_VALUE;
        long heightSum = 0L;
        BlockPosition highPoint = null;

        Map<BiomeId, Integer> biomeCounts = new HashMap<>();
        Map<ResourceTag, Double> resourceDensitySums = new EnumMap<>(ResourceTag.class);
        boolean[] isWaterCell = new boolean[gridSize];
        WaterFeatureType[] waterCellType = new WaterFeatureType[gridSize];
        boolean hasAnyWater = false;
        Map<BiomeId, BiomeSurveyData> lookupCache = new HashMap<>();

        for (int gz = 0; gz < gridDepth; gz++) {
            for (int gx = 0; gx < gridWidth; gx++) {
                TerrainSample sample = grid.getAtGrid(gx, gz);
                totalSamples++;

                int height = sample.height();
                if (height < heightMin) {
                    heightMin = height;
                }
                if (height > heightMax) {
                    heightMax = height;
                    highPoint = new BlockPosition(grid.originX() + gx * grid.sampleInterval(), height,
                            grid.originZ() + gz * grid.sampleInterval());
                }
                heightSum += height;

                biomeCounts.merge(sample.biome(), 1, Integer::sum);

                BiomeSurveyData surveyData = lookupCache.computeIfAbsent(sample.biome(), this.biomeLookup::lookup);
                for (Map.Entry<ResourceTag, Float> entry : surveyData.resourceDensities().entrySet()) {
                    resourceDensitySums.merge(entry.getKey(), (double) entry.getValue(), Double::sum);
                }

                if (surveyData.waterType() != null) {
                    int flatIndex = gz * gridWidth + gx;
                    isWaterCell[flatIndex] = true;
                    waterCellType[flatIndex] = surveyData.waterType();
                    hasAnyWater = true;
                }
            }
        }

        ElevationStats elevation = new ElevationStats(heightMin, heightMax, Math.toIntExact(heightSum / totalSamples), highPoint);
        Map<BiomeId, Float> biomeDistribution = toBiomeDistribution(biomeCounts, totalSamples);
        Map<ResourceTag, Float> resourceDensities = toResourceDensities(resourceDensitySums, totalSamples);
        Set<ResourceTag> resourceTags = deriveResourceTags(resourceDensities);
        Set<WaterFeatureType> waterFeatureTypes = hasAnyWater
                ? clusterWaterFeatures(gridWidth, gridDepth, isWaterCell, waterCellType)
                : EnumSet.noneOf(WaterFeatureType.class);

        return new SiteReport(bounds, grid, elevation, resourceDensities, biomeDistribution, waterFeatureTypes, resourceTags);
    }

    private static Map<BiomeId, Float> toBiomeDistribution(Map<BiomeId, Integer> biomeCounts, int totalSamples) {
        float totalSamplesFloat = (float) totalSamples;

        Map<BiomeId, Float> distribution = new LinkedHashMap<>();
        biomeCounts.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEach(entry -> distribution.put(entry.getKey(), entry.getValue() / totalSamplesFloat));

        return Collections.unmodifiableMap(distribution);
    }

    private static Map<ResourceTag, Float> toResourceDensities(Map<ResourceTag, Double> resourceDensitySums, int totalSamples) {
        Map<ResourceTag, Float> densities = new EnumMap<>(ResourceTag.class);
        for (Map.Entry<ResourceTag, Double> entry : resourceDensitySums.entrySet()) {
            densities.put(entry.getKey(), (float) (entry.getValue() / totalSamples));
        }

        return densities;
    }

    private static Set<ResourceTag> deriveResourceTags(Map<ResourceTag, Float> resourceDensities) {
        Set<ResourceTag> tags = EnumSet.noneOf(ResourceTag.class);
        for (ResourceTag tag : ResourceTag.values()) {
            if (resourceDensities.getOrDefault(tag, 0.0f) >= tag.threshold()) {
                tags.add(tag);
            }
        }
        return tags;
    }

    private static Set<WaterFeatureType> clusterWaterFeatures(int gridWidth,
                                                              int gridDepth,
                                                              boolean[] isWaterCell,
                                                              WaterFeatureType[] waterCellType) {
        int gridSize = gridWidth * gridDepth;
        int[] parent = new int[gridSize];
        int[] rank = new int[gridSize];
        for (int i = 0; i < gridSize; i++) {
            parent[i] = i;
        }

        for (int gz = 0; gz < gridDepth; gz++) {
            for (int gx = 0; gx < gridWidth; gx++) {
                int index = gz * gridWidth + gx;
                if (!isWaterCell[index]) {
                    continue;
                }
                if (gx + 1 < gridWidth && isWaterCell[index + 1]) {
                    union(parent, rank, index, index + 1);
                }
                if (gz + 1 < gridDepth && isWaterCell[index + gridWidth]) {
                    union(parent, rank, index, index + gridWidth);
                }
            }
        }

        Map<Integer, ClusterAccumulator> clusters = new HashMap<>();
        for (int i = 0; i < gridSize; i++) {
            if (!isWaterCell[i]) {
                continue;
            }

            int root = find(parent, i);
            ClusterAccumulator cluster = clusters.computeIfAbsent(root, unused -> new ClusterAccumulator());
            cluster.addCell(waterCellType[i]);

            int gx = i % gridWidth;
            int gz = i / gridWidth;
            if (gx == 0 || gx == gridWidth - 1 || gz == 0 || gz == gridDepth - 1) {
                cluster.markTouchesEdge();
            }
        }

        Set<WaterFeatureType> waterFeatureTypes = EnumSet.noneOf(WaterFeatureType.class);
        for (ClusterAccumulator cluster : clusters.values()) {
            WaterFeatureType type = cluster.touchesEdge ? cluster.majorityType() : WaterFeatureType.LAKE;
            waterFeatureTypes.add(type);
        }
        return waterFeatureTypes;
    }

    private static int find(int[] parent, int x) {
        while (parent[x] != x) {
            parent[x] = parent[parent[x]];
            x = parent[x];
        }
        return x;
    }

    private static void union(int[] parent, int[] rank, int a, int b) {
        int rootA = find(parent, a);
        int rootB = find(parent, b);
        if (rootA == rootB) {
            return;
        }

        if (rank[rootA] < rank[rootB]) {
            parent[rootA] = rootB;
        } else if (rank[rootA] > rank[rootB]) {
            parent[rootB] = rootA;
        } else {
            parent[rootB] = rootA;
            rank[rootA]++;
        }
    }

    private static final class ClusterAccumulator {

        private int riverCount;
        private int oceanCount;
        private boolean touchesEdge;

        private void addCell(WaterFeatureType type) {
            if (type == WaterFeatureType.RIVER) {
                this.riverCount++;
            } else {
                // Raw biome survey data currently only marks water cells as RIVER or OCEAN.
                // LAKE is derived at the cluster level when a water body does not touch the grid edge,
                // so any non-RIVER raw type is intentionally counted on the ocean-side of the majority split.
                this.oceanCount++;
            }
        }

        private void markTouchesEdge() {
            this.touchesEdge = true;
        }

        private WaterFeatureType majorityType() {
            return this.riverCount >= this.oceanCount ? WaterFeatureType.RIVER : WaterFeatureType.OCEAN;
        }

    }

}
