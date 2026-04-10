package dev.breezes.settlements.domain.generation.layout;

import dev.breezes.settlements.domain.generation.model.geometry.BlockPosition;
import dev.breezes.settlements.domain.generation.model.survey.ResourceTag;
import dev.breezes.settlements.domain.generation.model.survey.TerrainGrid;
import dev.breezes.settlements.domain.generation.model.survey.TerrainSample;
import dev.breezes.settlements.domain.generation.model.survey.WaterFeatureType;
import dev.breezes.settlements.domain.generation.survey.BiomeSurveyData;
import dev.breezes.settlements.domain.generation.survey.BiomeSurveyLookup;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LocalResourceScanner {

    private final BiomeSurveyLookup biomeLookup;

    public LocalResourceScanner(BiomeSurveyLookup biomeLookup) {
        this.biomeLookup = biomeLookup;
    }

    public Set<ResourceTag> scan(TerrainGrid grid, BlockPosition center, int radius) {
        int interval = grid.sampleInterval();
        int minX = Math.max(grid.originX(), center.x() - radius);
        int maxX = Math.min(grid.originX() + (grid.gridWidth() - 1) * interval, center.x() + radius);
        int minZ = Math.max(grid.originZ(), center.z() - radius);
        int maxZ = Math.min(grid.originZ() + (grid.gridDepth() - 1) * interval, center.z() + radius);

        Map<ResourceTag, Float> totals = new EnumMap<>(ResourceTag.class);
        int count = 0;
        for (int worldZ = minZ; worldZ <= maxZ; worldZ += interval) {
            for (int worldX = minX; worldX <= maxX; worldX += interval) {
                TerrainSample sample = grid.getAtWorld(worldX, worldZ);
                BiomeSurveyData surveyData = this.biomeLookup.lookup(sample.biome());
                for (Map.Entry<ResourceTag, Float> entry : surveyData.resourceDensities().entrySet()) {
                    totals.merge(entry.getKey(), entry.getValue(), Float::sum);
                }
                count++;
            }
        }

        if (count == 0) {
            return Collections.emptySet();
        }
        
        Set<ResourceTag> found = new HashSet<>();
        for (ResourceTag tag : ResourceTag.values()) {
            float average = totals.getOrDefault(tag, 0.0f) / count;
            if (average >= tag.threshold()) {
                found.add(tag);
            }
        }
        return found;
    }

    public boolean isWaterAt(TerrainGrid grid, int worldX, int worldZ) {
        return this.surveyDataAt(grid, worldX, worldZ).waterType() != null;
    }

    public WaterFeatureType waterTypeAt(TerrainGrid grid, int worldX, int worldZ) {
        return this.surveyDataAt(grid, worldX, worldZ).waterType();
    }

    private BiomeSurveyData surveyDataAt(TerrainGrid grid, int worldX, int worldZ) {
        TerrainSample sample = grid.getAtWorld(worldX, worldZ);
        return this.biomeLookup.lookup(sample.biome());
    }

}
