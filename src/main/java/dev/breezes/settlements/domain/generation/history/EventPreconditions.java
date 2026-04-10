package dev.breezes.settlements.domain.generation.history;

import dev.breezes.settlements.domain.generation.model.profile.SettlementProfile;
import dev.breezes.settlements.domain.generation.model.profile.TraitId;
import dev.breezes.settlements.domain.generation.model.survey.ResourceTag;
import dev.breezes.settlements.domain.generation.model.survey.SiteReport;
import dev.breezes.settlements.domain.generation.model.survey.WaterFeatureType;
import lombok.Builder;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Builder
public record EventPreconditions(
        Map<TraitId, Float> minTraitWeights,
        Set<ResourceTag> requiredResourceTags,
        Set<WaterFeatureType> requiredWaterFeatures,
        int minPopulation
) {

    public static final EventPreconditions NONE = new EventPreconditions(Map.of(), Set.of(), Set.of(), 0);

    public EventPreconditions {
        minTraitWeights = minTraitWeights == null ? Map.of() : new LinkedHashMap<>(minTraitWeights);
        requiredResourceTags = requiredResourceTags == null ? Set.of() : new LinkedHashSet<>(requiredResourceTags);
        requiredWaterFeatures = requiredWaterFeatures == null ? Set.of() : new LinkedHashSet<>(requiredWaterFeatures);
    }

    public boolean isSatisfiedBy(SettlementProfile profile, SiteReport report) {
        for (Map.Entry<TraitId, Float> entry : this.minTraitWeights.entrySet()) {
            float current = profile.adjustedWeights().getOrDefault(entry.getKey(), 0.0f);
            if (current < entry.getValue()) {
                return false;
            }
        }

        if (!report.resourceTags().containsAll(this.requiredResourceTags)) {
            return false;
        }

        if (!this.requiredWaterFeatures.isEmpty()
                && Collections.disjoint(report.waterFeatureTypes(), this.requiredWaterFeatures)) {
            return false;
        }

        return profile.estimatedPopulation() >= this.minPopulation;
    }

}
