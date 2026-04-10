package dev.breezes.settlements.domain.generation.layout;

import dev.breezes.settlements.domain.generation.model.building.BuildingAssignment;
import dev.breezes.settlements.domain.generation.model.building.BuildingDefinition;
import dev.breezes.settlements.domain.generation.model.geometry.Direction;
import dev.breezes.settlements.domain.generation.model.layout.Plot;
import dev.breezes.settlements.domain.generation.model.layout.ZoneTier;
import dev.breezes.settlements.domain.generation.model.profile.TraitId;
import dev.breezes.settlements.domain.generation.model.survey.ResourceTag;

import javax.annotation.Nullable;
import java.util.Set;

public abstract class BuildingPlacer {

    public abstract PlacementPhaseResult executePlacement(PlacementContext context);

    protected BuildingAssignment createAssignment(int plotId,
                                                  BuildingDefinition building,
                                                  @Nullable TraitId traitSource,
                                                  LayoutSupport.CandidateFootprint footprint,
                                                  Direction facing,
                                                  ZoneTier zone,
                                                  boolean roadFrontage,
                                                  Set<ResourceTag> localResources) {
        Plot plot = Plot.builder()
                .id(plotId)
                .zone(zone)
                .bounds(footprint.bounds())
                .facing(facing)
                .targetY(footprint.targetY())
                .maxElevationDelta(footprint.elevationDelta())
                .roadFrontage(roadFrontage)
                .localResources(localResources)
                .build();
        return new BuildingAssignment(building, plot, facing, traitSource);
    }

    @Nullable
    protected TraitId resolveTraitSource(BuildingDefinition building, PlacementContext context) {
        TraitId overridden = context.getTraitSourceOverrides().get(building.id());
        return overridden != null ? overridden : LayoutSupport.dominantTrait(building, context.getProfile());
    }

}
