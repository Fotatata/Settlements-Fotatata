package dev.breezes.settlements.domain.generation.model.profile;

import lombok.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Builder
public record SettlementProfile(
        TraitId primary,
        List<TraitId> secondary,
        List<TraitId> flavor,
        Map<TraitId, Float> adjustedWeights,
        ScaleTier scaleTier,
        int estimatedPopulation,
        float wealthLevel,
        DefenseLevel defenseLevel,
        long seed,
        List<String> historyEventIds
) {

    public SettlementProfile {
        secondary = List.copyOf(secondary);
        flavor = List.copyOf(flavor);
        adjustedWeights = Map.copyOf(adjustedWeights);
        historyEventIds = List.copyOf(historyEventIds);
    }

    public boolean hasTrait(TraitId id) {
        return this.primary == id || this.secondary.contains(id) || this.flavor.contains(id);
    }

    public TraitSlot getTraitSlot(TraitId id) {
        if (this.primary == id) {
            return TraitSlot.PRIMARY;
        }
        if (this.secondary.contains(id)) {
            return TraitSlot.SECONDARY;
        }
        if (this.flavor.contains(id)) {
            return TraitSlot.FLAVOR;
        }
        return null;
    }

    public List<TraitId> allTraits() {
        List<TraitId> all = new ArrayList<>(1 + this.secondary.size() + this.flavor.size());
        all.add(this.primary);
        all.addAll(this.secondary);
        all.addAll(this.flavor);
        return List.copyOf(all);
    }

}
