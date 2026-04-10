package dev.breezes.settlements.domain.generation.model.survey;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ResourceTag {

    LUMBER(0.15f),
    FRESHWATER(0.01f),
    COASTAL(0.05f),
    STONE(0.20f),
    ORE_BEARING(0.20f),
    FLORAL(0.10f),
    ARID(0.30f),
    FROZEN(0.30f);

    private final float threshold;

    public float threshold() {
        return this.threshold;
    }

}
