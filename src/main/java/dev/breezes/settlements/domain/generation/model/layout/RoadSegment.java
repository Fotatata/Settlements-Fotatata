package dev.breezes.settlements.domain.generation.model.layout;

import dev.breezes.settlements.domain.generation.model.geometry.BlockPosition;

public record RoadSegment(
        BlockPosition start,
        BlockPosition end,
        RoadType type
) {
}
