package dev.breezes.settlements.domain.generation.model.survey;

import dev.breezes.settlements.domain.common.BiomeId;

import java.util.Map;
import java.util.Set;

public record SiteReport(
        SurveyBounds bounds,
        TerrainGrid terrainGrid,
        ElevationStats elevation,
        Map<ResourceTag, Float> resourceDensities,
        Map<BiomeId, Float> biomeDistribution,
        Set<WaterFeatureType> waterFeatureTypes,
        Set<ResourceTag> resourceTags
) {

    public SiteReport {
        resourceDensities = Map.copyOf(resourceDensities);
        biomeDistribution = Map.copyOf(biomeDistribution);
        waterFeatureTypes = Set.copyOf(waterFeatureTypes);
        resourceTags = Set.copyOf(resourceTags);
    }

}
