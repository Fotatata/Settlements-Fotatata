package dev.breezes.settlements.domain.generation.model.survey;

import dev.breezes.settlements.domain.generation.model.geometry.BoundingRegion;

public record SurveyBounds(
        BoundingRegion buildArea,
        BoundingRegion sampleArea,
        int padding
) {

    public SurveyBounds {
        if (padding < 0) {
            throw new IllegalArgumentException("padding must be >= 0");
        }
    }

    public static SurveyBounds fromBuildArea(BoundingRegion buildArea, int padding) {
        return new SurveyBounds(buildArea, buildArea.expandedBy(padding), padding);
    }

}
