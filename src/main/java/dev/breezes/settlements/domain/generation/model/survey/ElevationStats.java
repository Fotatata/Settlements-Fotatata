package dev.breezes.settlements.domain.generation.model.survey;

import dev.breezes.settlements.domain.generation.model.geometry.BlockPosition;

public record ElevationStats(
        int min,
        int max,
        int mean,
        BlockPosition highPoint
) {

    public ElevationStats {
        if (max < min) {
            throw new IllegalArgumentException("max must be >= min");
        }
    }

}
