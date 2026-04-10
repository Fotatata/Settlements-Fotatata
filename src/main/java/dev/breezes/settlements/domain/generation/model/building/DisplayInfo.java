package dev.breezes.settlements.domain.generation.model.building;

import javax.annotation.Nullable;

public record DisplayInfo(
        String displayName,
        String description,
        @Nullable String customName,
        String iconItemId
) {
}
