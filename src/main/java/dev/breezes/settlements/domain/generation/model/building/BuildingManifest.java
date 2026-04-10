package dev.breezes.settlements.domain.generation.model.building;

import java.util.List;

public record BuildingManifest(List<BuildingDefinition> buildings) {

    public BuildingManifest {
        buildings = List.copyOf(buildings);
    }

}
