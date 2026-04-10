package dev.breezes.settlements.infrastructure.minecraft.data.validation;

import dev.breezes.settlements.domain.generation.model.building.BuildingDefinition;
import dev.breezes.settlements.domain.generation.model.profile.TraitId;
import dev.breezes.settlements.domain.generation.scoring.TraitScorer;
import dev.breezes.settlements.domain.generation.trait.TraitRegistry;
import dev.breezes.settlements.infrastructure.minecraft.data.building.BuildingDefinitionDataManager;
import dev.breezes.settlements.infrastructure.minecraft.data.scoring.TraitScorerDataManager;
import lombok.CustomLog;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@CustomLog
public final class GenerationDataValidator {

    public void validateAndApply(@Nonnull TraitRegistry traitRegistry,
                                 @Nonnull TraitScorerDataManager scorerDataManager,
                                 @Nonnull BuildingDefinitionDataManager buildingDataManager) {
        Set<TraitId> knownTraits = traitRegistry.allTraitIds();

        Map<TraitId, TraitScorer> validScorers = new LinkedHashMap<>();
        for (Map.Entry<TraitId, TraitScorer> entry : scorerDataManager.rawScorers().entrySet()) {
            if (!knownTraits.contains(entry.getKey())) {
                log.error("Trait scorer registered for unknown trait '{}'", entry.getKey());
                continue;
            }
            validScorers.put(entry.getKey(), entry.getValue());
        }

        List<BuildingDefinition> validBuildings = new ArrayList<>();
        for (BuildingDefinition definition : buildingDataManager.rawDefinitions()) {
            boolean invalid = false;
            for (TraitId traitId : definition.traitAffinities().keySet()) {
                if (!knownTraits.contains(traitId)) {
                    log.error("Building '{}' references unknown trait '{}'", definition.id(), traitId);
                    invalid = true;
                }
            }
            if (!invalid) {
                validBuildings.add(definition);
            }
        }

        scorerDataManager.replaceActiveScorers(validScorers);
        buildingDataManager.replaceActiveDefinitions(validBuildings);
        log.info("Validated generation data against {} known traits: {} scorers active, {} buildings active",
                knownTraits.size(), validScorers.size(), validBuildings.size());
    }

}
