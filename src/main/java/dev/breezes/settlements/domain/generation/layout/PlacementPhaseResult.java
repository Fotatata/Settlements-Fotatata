package dev.breezes.settlements.domain.generation.layout;

import dev.breezes.settlements.domain.generation.model.building.BuildingAssignment;
import dev.breezes.settlements.domain.generation.model.building.BuildingDefinition;

import java.util.List;

public record PlacementPhaseResult(
        List<BuildingAssignment> placed,
        List<BuildingDefinition> remaining
) {

    public PlacementPhaseResult {
        placed = List.copyOf(placed);
        remaining = List.copyOf(remaining);
    }

}
