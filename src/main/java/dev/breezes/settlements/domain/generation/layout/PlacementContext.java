package dev.breezes.settlements.domain.generation.layout;

import dev.breezes.settlements.domain.generation.model.building.BuildingDefinition;
import dev.breezes.settlements.domain.generation.model.geometry.BlockPosition;
import dev.breezes.settlements.domain.generation.model.geometry.BoundingRegion;
import dev.breezes.settlements.domain.generation.model.layout.RoadSegment;
import dev.breezes.settlements.domain.generation.model.profile.ScaleTier;
import dev.breezes.settlements.domain.generation.model.profile.SettlementProfile;
import dev.breezes.settlements.domain.generation.model.profile.TraitId;
import dev.breezes.settlements.domain.generation.model.survey.SiteReport;
import dev.breezes.settlements.domain.generation.model.survey.TerrainGrid;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Random;

@Getter
@Builder
public class PlacementContext {

    private final SiteReport report;
    private final SettlementProfile profile;
    private final LocalResourceScanner resourceScanner;
    private final PlacementGrid grid;
    private final PlacementValidator validator;
    private final BlockPosition anchor;
    private final Random random;

    @Setter
    private BlockPosition planningCenter;

    @Setter
    @Builder.Default
    private List<BuildingDefinition> buildings = List.of();

    @Setter
    @Builder.Default
    private List<RoadSegment> roads = List.of();

    @Setter
    @Builder.Default
    private int startingPlotId = 0;

    @Setter
    @Builder.Default
    private Map<String, TraitId> traitSourceOverrides = Map.of();

    public BoundingRegion buildArea() {
        return this.report.bounds().buildArea();
    }

    public TerrainGrid terrainGrid() {
        return this.report.terrainGrid();
    }

    public ScaleTier scaleTier() {
        return this.profile.scaleTier();
    }

}
