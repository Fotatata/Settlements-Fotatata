package dev.breezes.settlements.domain.generation.model;

import dev.breezes.settlements.domain.generation.history.HistoryEventResult;
import dev.breezes.settlements.domain.generation.layout.LayoutResult;
import dev.breezes.settlements.domain.generation.model.profile.SettlementProfile;
import dev.breezes.settlements.domain.generation.model.survey.SiteReport;

public record GenerationResult(
        SiteReport siteReport,
        SettlementProfile profile,
        HistoryEventResult history,
        LayoutResult layout,
        long generationSeed
) {
}
