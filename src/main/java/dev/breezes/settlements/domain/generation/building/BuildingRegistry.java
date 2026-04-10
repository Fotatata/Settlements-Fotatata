package dev.breezes.settlements.domain.generation.building;

import dev.breezes.settlements.domain.generation.model.building.BuildingDefinition;
import dev.breezes.settlements.domain.generation.model.profile.TraitId;

import java.util.List;
import java.util.Optional;

public interface BuildingRegistry {

    List<BuildingDefinition> allBuildings();
 
    List<BuildingDefinition> constrainedBuildings();

    List<BuildingDefinition> unconstrainedBuildings();

    List<BuildingDefinition> forTrait(TraitId trait);

    Optional<BuildingDefinition> byId(String id);

}
