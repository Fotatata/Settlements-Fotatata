package dev.breezes.settlements.domain.generation.model.building;

public record GlobalAffinity(
        String triggerBuildingOrTraitId,
        float probabilityBoost
) {

    public GlobalAffinity {
        if (triggerBuildingOrTraitId == null || triggerBuildingOrTraitId.isBlank()) {
            throw new IllegalArgumentException("triggerBuildingOrTraitId must not be blank");
        }
        if (probabilityBoost < 0.0f) {
            throw new IllegalArgumentException("probabilityBoost must be >= 0");
        }
    }

}
