package dev.breezes.settlements.domain.generation.model.survey;

import dev.breezes.settlements.domain.common.BiomeId;

public record TerrainSample(
        int height,
        BiomeId biome,
        float temperature
) {

    public TerrainSample {
        if (biome == null) {
            throw new IllegalArgumentException("biome must not be null");
        }
    }

}
