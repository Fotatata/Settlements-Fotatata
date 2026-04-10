package dev.breezes.settlements.domain.generation.building;

import dev.breezes.settlements.domain.generation.model.building.BuildingDefinition;
import dev.breezes.settlements.domain.generation.model.building.ResolvedTemplate;

import java.util.Optional;
import java.util.Random;

public interface TemplateResolver {

    Optional<ResolvedTemplate> resolve(BuildingDefinition building, Random random, TemplateResolutionContext context);

}
