package dev.breezes.settlements.domain.generation.scoring;

import dev.breezes.settlements.domain.generation.model.profile.TraitId;
import dev.breezes.settlements.domain.generation.model.survey.SiteReport;
import lombok.AllArgsConstructor;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.Map;

@AllArgsConstructor
public class ScoreEngine {

    private final TraitScorerRegistry registry;

    public Map<TraitId, Float> score(@Nonnull SiteReport report) {
        Map<TraitId, Float> scores = new LinkedHashMap<>();
        for (Map.Entry<TraitId, TraitScorer> entry : this.registry.allScorers().entrySet()) {
            scores.put(entry.getKey(), entry.getValue().score(report));
        }

        return scores;
    }

}
