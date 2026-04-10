package dev.breezes.settlements.domain.generation.scoring;

import dev.breezes.settlements.domain.generation.model.survey.SiteReport;

@FunctionalInterface
public interface TraitScorer {

    float score(SiteReport report);
 
}
