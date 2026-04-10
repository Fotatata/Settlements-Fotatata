package dev.breezes.settlements.domain.generation.trait;

import dev.breezes.settlements.domain.generation.model.profile.TraitDefinition;
import dev.breezes.settlements.domain.generation.model.profile.TraitId;

import java.util.Optional;
import java.util.Set;

public interface TraitRegistry {

    Set<TraitId> allTraitIds();

    Optional<TraitDefinition> byId(TraitId id);

}
