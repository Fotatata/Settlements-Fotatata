package dev.breezes.settlements.domain.generation.model.building;

public record ProximityAffinity(
        String targetBuildingId,
        float peakBonus,
        float decayRate
) {

    public ProximityAffinity {
        if (targetBuildingId == null || targetBuildingId.isBlank()) {
            throw new IllegalArgumentException("targetBuildingId must not be blank");
        }
        if (peakBonus < 0.0f || decayRate < 0.0f) {
            throw new IllegalArgumentException("peakBonus and decayRate must be >= 0");
        }
    }

    public double bonusAtDistance(double distance) {
        if (distance < 0.0d) {
            throw new IllegalArgumentException("distance must be >= 0");
        }
        return this.peakBonus * Math.exp(-this.decayRate * distance);
    }
}
