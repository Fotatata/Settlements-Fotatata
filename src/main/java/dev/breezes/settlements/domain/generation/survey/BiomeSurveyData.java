package dev.breezes.settlements.domain.generation.survey;

import dev.breezes.settlements.domain.generation.model.survey.ResourceTag;
import dev.breezes.settlements.domain.generation.model.survey.WaterFeatureType;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public record BiomeSurveyData(
        Map<ResourceTag, Float> resourceDensities,
        @Nullable WaterFeatureType waterType,
        Set<String> templateTags
) {

    public static final BiomeSurveyData DEFAULT = new BiomeSurveyData(Map.of(), null, Set.of());

    public BiomeSurveyData {
        resourceDensities = Map.copyOf(resourceDensities);
        templateTags = Set.copyOf(templateTags == null ? Set.of() : templateTags);
    }

}
