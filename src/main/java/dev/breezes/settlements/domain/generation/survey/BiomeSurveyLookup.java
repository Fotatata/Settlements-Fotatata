package dev.breezes.settlements.domain.generation.survey;

import dev.breezes.settlements.domain.common.BiomeId;

@FunctionalInterface
public interface BiomeSurveyLookup {

    BiomeSurveyData lookup(BiomeId biome);

}
