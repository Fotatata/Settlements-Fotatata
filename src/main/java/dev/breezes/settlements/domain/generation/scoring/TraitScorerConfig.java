package dev.breezes.settlements.domain.generation.scoring;

import dev.breezes.settlements.domain.common.BiomeId;
import dev.breezes.settlements.domain.generation.model.profile.TraitId;
import dev.breezes.settlements.domain.generation.model.survey.ResourceTag;
import dev.breezes.settlements.domain.generation.model.survey.WaterFeatureType;

import java.util.Map;
import java.util.Set;

public record TraitScorerConfig(
        TraitId trait,
        float baseScore,
        Map<ResourceTag, Float> resourceTagWeights,
        Set<ResourceTag> requiredTags,
        Set<ResourceTag> vetoTags,
        Map<WaterFeatureType, Float> waterFeatureWeights,
        Map<BiomeId, Float> biomeWeights,
        float elevationDeltaWeight,
        float elevationDeltaNormalization
) {

    public TraitScorerConfig {
        resourceTagWeights = Map.copyOf(resourceTagWeights);
        requiredTags = Set.copyOf(requiredTags);
        vetoTags = Set.copyOf(vetoTags);
        waterFeatureWeights = Map.copyOf(waterFeatureWeights);
        biomeWeights = Map.copyOf(biomeWeights);
    }

}
