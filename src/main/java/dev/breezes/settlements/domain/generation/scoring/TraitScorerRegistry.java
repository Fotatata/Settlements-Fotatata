package dev.breezes.settlements.domain.generation.scoring;

import dev.breezes.settlements.domain.generation.model.profile.TraitId;

import java.util.Map;

public interface TraitScorerRegistry {

    Map<TraitId, TraitScorer> allScorers();

}
