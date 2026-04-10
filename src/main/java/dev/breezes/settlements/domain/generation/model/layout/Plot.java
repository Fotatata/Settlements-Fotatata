package dev.breezes.settlements.domain.generation.model.layout;

import dev.breezes.settlements.domain.generation.model.geometry.BoundingRegion;
import dev.breezes.settlements.domain.generation.model.geometry.Direction;
import dev.breezes.settlements.domain.generation.model.survey.ResourceTag;
import lombok.Builder;

import java.util.Set;

@Builder
public record Plot(
        int id,
        ZoneTier zone,
        BoundingRegion bounds,
        Direction facing,
        int targetY,
        int maxElevationDelta,
        boolean roadFrontage,
        Set<ResourceTag> localResources
) {

    public Plot {
        if (id < 0) {
            throw new IllegalArgumentException("id must be >= 0");
        }
        if (maxElevationDelta < 0) {
            throw new IllegalArgumentException("maxElevationDelta must be >= 0");
        }
        localResources = Set.copyOf(localResources);
    }

}
