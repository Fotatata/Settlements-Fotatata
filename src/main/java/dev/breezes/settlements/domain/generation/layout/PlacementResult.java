package dev.breezes.settlements.domain.generation.layout;

import dev.breezes.settlements.domain.generation.model.survey.ResourceTag;

import javax.annotation.Nullable;
import java.util.Set;

public record PlacementResult(
        boolean valid,
        @Nullable RejectionReason rejection,
        @Nullable Set<ResourceTag> localResources
) {

    public PlacementResult {
        localResources = localResources == null ? null : Set.copyOf(localResources);
    }

    public static PlacementResult accepted(Set<ResourceTag> localResources) {
        return new PlacementResult(true, null, localResources);
    }

    public static PlacementResult rejected(RejectionReason rejection) {
        return new PlacementResult(false, rejection, null);
    }

    public enum RejectionReason {
        OUTSIDE_BUILD_AREA,
        TOO_STEEP,
        GRID_CONFLICT,
        MISSING_REQUIRED_RESOURCE,
        FORBIDDEN_RESOURCE_PRESENT,
        ON_WATER
    }

}
