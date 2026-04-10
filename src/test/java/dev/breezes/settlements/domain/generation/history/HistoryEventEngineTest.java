package dev.breezes.settlements.domain.generation.history;

import dev.breezes.settlements.domain.generation.model.profile.DefenseLevel;
import dev.breezes.settlements.domain.generation.model.profile.ScaleTier;
import dev.breezes.settlements.domain.generation.model.profile.SettlementProfile;
import dev.breezes.settlements.domain.generation.model.profile.TraitDefinition;
import dev.breezes.settlements.domain.generation.model.profile.TraitId;
import dev.breezes.settlements.domain.generation.model.geometry.BlockPosition;
import dev.breezes.settlements.domain.generation.model.survey.ElevationStats;
import dev.breezes.settlements.domain.generation.model.survey.ResourceTag;
import dev.breezes.settlements.domain.generation.model.survey.SiteReport;
import dev.breezes.settlements.domain.generation.model.survey.WaterFeatureType;
import dev.breezes.settlements.domain.generation.trait.TraitRegistry;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HistoryEventEngineTest {

    private static final TraitId LUMBER = TraitId.of("settlements:settlement_traits/lumber");
    private static final TraitId DEFENSE = TraitId.of("settlements:settlement_traits/defense");
    private static final TraitId CRAFT = TraitId.of("settlements:settlement_traits/craft");
    private static final TraitId FISHING = TraitId.of("settlements:settlement_traits/fishing");
    private static final TraitId FARMING = TraitId.of("settlements:settlement_traits/farming");
    private static final TraitId ARCANE = TraitId.of("settlements:settlement_traits/arcane");
    private static final TraitId UNKNOWN = TraitId.of("settlements:settlement_traits/unknown_history_trait");

    @Test
    void sameSeed_producesDeterministicResult() {
        HistoryEventRegistry registry = () -> List.of(
                event("settlements:event/a", 100, 100, List.of("FOUNDING"), Map.of(DEFENSE, 0.1f), List.of("a"), EventPreconditions.NONE),
                event("settlements:event/b", 210, 210, List.of(), Map.of(CRAFT, 0.1f), List.of("b"), EventPreconditions.NONE)
        );
        HistoryEventEngine engine = new HistoryEventEngine(registry, traitRegistry(LUMBER, DEFENSE, CRAFT));

        HistoryEventResult first = engine.roll(profile(Map.of(LUMBER, 0.5f, DEFENSE, 0.2f)), report(Set.of(), Set.of()), new Random(12345L));
        HistoryEventResult second = engine.roll(profile(Map.of(LUMBER, 0.5f, DEFENSE, 0.2f)), report(Set.of(), Set.of()), new Random(12345L));

        assertEquals(first, second);
    }

    @Test
    void exclusiveTags_keepEarliestRolledEvent() {
        HistoryEventRegistry registry = () -> List.of(
                event("settlements:event/founding_b", 150, 150, List.of("FOUNDING"), Map.of(DEFENSE, 0.1f), List.of("marker_b"), EventPreconditions.NONE),
                event("settlements:event/founding_a", 100, 100, List.of("FOUNDING"), Map.of(CRAFT, 0.1f), List.of("marker_a"), EventPreconditions.NONE),
                event("settlements:event/other", 220, 220, List.of(), Map.of(LUMBER, 0.05f), List.of("other"), EventPreconditions.NONE)
        );

        HistoryEventEngine engine = new HistoryEventEngine(registry, traitRegistry(LUMBER, DEFENSE, CRAFT));
        HistoryEventResult result = engine.roll(profile(Map.of(LUMBER, 0.5f, DEFENSE, 0.2f)), report(Set.of(), Set.of()), new Random(1L));

        assertEquals(List.of("settlements:event/founding_a", "settlements:event/other"), result.eventIds());
        assertEquals(Set.of("marker_a", "other"), result.visualMarkers().markers());
    }

    @Test
    void preconditions_filterEvents() {
        EventPreconditions requiresLumber = EventPreconditions.builder()
                .minTraitWeights(Map.of(LUMBER, 0.3f))
                .build();
        HistoryEventRegistry registry = () -> List.of(
                event("settlements:event/fire", 200, 200, List.of(), Map.of(), List.of(), requiresLumber)
        );

        HistoryEventEngine engine = new HistoryEventEngine(registry, traitRegistry(LUMBER));
        HistoryEventResult result = engine.roll(profile(Map.of(LUMBER, 0.2f)), report(Set.of(), Set.of()), new Random(2L));

        assertTrue(result.eventIds().isEmpty());
        assertEquals(Map.of(LUMBER, 0.2f), result.modifiedWeights());
    }

    @Test
    void modifiersClampAndCanInsertKnownTraits() {
        HistoryEventRegistry registry = () -> List.of(
                event("settlements:event/boost", 100, 100, List.of(), Map.of(DEFENSE, 0.9f, ARCANE, 0.3f), List.of(), EventPreconditions.NONE),
                event("settlements:event/reduce", 200, 200, List.of(), Map.of(DEFENSE, -1.5f), List.of(), EventPreconditions.NONE)
        );

        HistoryEventEngine engine = new HistoryEventEngine(registry, traitRegistry(DEFENSE, ARCANE));
        HistoryEventResult result = engine.roll(profile(Map.of(DEFENSE, 0.4f)), report(Set.of(), Set.of()), new Random(3L));

        assertEquals(0.0f, result.modifiedWeights().get(DEFENSE), 0.0001f);
        assertEquals(0.3f, result.modifiedWeights().get(ARCANE), 0.0001f);
    }

    @Test
    void unknownTraitModifierIsSkipped() {
        HistoryEventRegistry registry = () -> List.of(
                event("settlements:event/unknown", 100, 100, List.of(), Map.of(UNKNOWN, 0.4f, DEFENSE, 0.1f), List.of(), EventPreconditions.NONE)
        );

        HistoryEventEngine engine = new HistoryEventEngine(registry, traitRegistry(DEFENSE));
        HistoryEventResult result = engine.roll(profile(Map.of(DEFENSE, 0.2f)), report(Set.of(), Set.of()), new Random(4L));

        assertEquals(Map.of(DEFENSE, 0.3f), result.modifiedWeights());
    }

    @Test
    void emptyEligiblePoolReturnsPassthroughResult() {
        EventPreconditions requiresWater = EventPreconditions.builder()
                .requiredWaterFeatures(Set.of(WaterFeatureType.RIVER))
                .build();
        HistoryEventRegistry registry = () -> List.of(
                event("settlements:event/water", 100, 100, List.of(), Map.of(DEFENSE, 0.1f), List.of("marker"), requiresWater)
        );

        HistoryEventEngine engine = new HistoryEventEngine(registry, traitRegistry(DEFENSE));
        HistoryEventResult result = engine.roll(profile(Map.of(DEFENSE, 0.2f)), report(Set.of(), Set.of()), new Random(5L));

        assertTrue(result.eventIds().isEmpty());
        assertTrue(result.visualMarkers().isEmpty());
        assertEquals(Map.of(DEFENSE, 0.2f), result.modifiedWeights());
    }

    @Test
    void truncatesAtScaleTierMaximumHistoryEvents() {
        HistoryEventRegistry registry = () -> List.of(
                event("settlements:event/one", 100, 100, List.of(), Map.of(), List.of(), EventPreconditions.NONE),
                event("settlements:event/two", 110, 110, List.of(), Map.of(), List.of(), EventPreconditions.NONE),
                event("settlements:event/three", 120, 120, List.of(), Map.of(), List.of(), EventPreconditions.NONE)
        );

        HistoryEventEngine engine = new HistoryEventEngine(registry, traitRegistry());
        HistoryEventResult result = engine.roll(profile(Map.of(), ScaleTier.HAMLET), report(Set.of(), Set.of()), new Random(6L));

        assertEquals(2, result.eventIds().size());
        assertEquals(List.of("settlements:event/one", "settlements:event/two"), result.eventIds());
    }

    @Test
    void waterFeaturePreconditionUsesOrLogic() {
        EventPreconditions requiresAnyWater = EventPreconditions.builder()
                .requiredWaterFeatures(Set.of(WaterFeatureType.RIVER, WaterFeatureType.OCEAN))
                .build();
        HistoryEventRegistry registry = () -> List.of(
                event("settlements:event/flood", 100, 100, List.of(), Map.of(), List.of(), requiresAnyWater)
        );

        HistoryEventEngine engine = new HistoryEventEngine(registry, traitRegistry());
        HistoryEventResult result = engine.roll(profile(Map.of()), report(Set.of(WaterFeatureType.RIVER), Set.of()), new Random(7L));

        assertEquals(List.of("settlements:event/flood"), result.eventIds());
    }

    @Test
    void visualMarkersAccumulateInTimeOrder() {
        HistoryEventRegistry registry = () -> List.of(
                event("settlements:event/late", 220, 220, List.of(), Map.of(), List.of("late_a", "late_b"), EventPreconditions.NONE),
                event("settlements:event/early", 120, 120, List.of(), Map.of(), List.of("early"), EventPreconditions.NONE)
        );

        HistoryEventEngine engine = new HistoryEventEngine(registry, traitRegistry());
        HistoryEventResult result = engine.roll(profile(Map.of()), report(Set.of(), Set.of()), new Random(8L));

        assertEquals(List.of("settlements:event/early", "settlements:event/late"), result.eventIds());
        assertEquals(Set.of("early", "late_a", "late_b"), result.visualMarkers().markers());
    }

    private static HistoryEventDefinition event(String id,
                                                int min,
                                                int max,
                                                List<String> exclusiveTags,
                                                Map<TraitId, Float> modifiers,
                                                List<String> visualMarkers,
                                                EventPreconditions preconditions) {
        return HistoryEventDefinition.builder()
                .id(id)
                .category("test")
                .timeHorizonMin(min)
                .timeHorizonMax(max)
                .exclusiveTags(exclusiveTags)
                .probabilityWeight(1.0f)
                .preconditions(preconditions)
                .traitModifiers(modifiers)
                .visualMarkers(visualMarkers)
                .narrativeText("test")
                .build();
    }

    private static SettlementProfile profile(Map<TraitId, Float> adjustedWeights) {
        return profile(adjustedWeights, ScaleTier.VILLAGE);
    }

    private static SettlementProfile profile(Map<TraitId, Float> adjustedWeights, ScaleTier scaleTier) {
        List<TraitId> traits = new ArrayList<>(adjustedWeights.keySet());
        TraitId primary = traits.isEmpty() ? DEFENSE : traits.getFirst();
        return SettlementProfile.builder()
                .primary(primary)
                .secondary(List.of())
                .flavor(List.of())
                .adjustedWeights(new LinkedHashMap<>(adjustedWeights))
                .scaleTier(scaleTier)
                .estimatedPopulation(40)
                .wealthLevel(0.5f)
                .defenseLevel(DefenseLevel.NONE)
                .seed(123L)
                .historyEventIds(List.of())
                .build();
    }

    private static SiteReport report(Set<WaterFeatureType> waterFeatures, Set<ResourceTag> resourceTags) {
        return new SiteReport(
                null,
                null,
                new ElevationStats(64, 64, 64, new BlockPosition(0, 64, 0)),
                Map.of(),
                Map.of(),
                waterFeatures,
                resourceTags
        );
    }

    @SafeVarargs
    private static TraitRegistry traitRegistry(TraitId... traitIds) {
        Set<TraitId> ids = Set.of(traitIds);
        return new TraitRegistry() {
            @Override
            public Set<TraitId> allTraitIds() {
                return ids;
            }

            @Override
            public Optional<TraitDefinition> byId(TraitId id) {
                return Optional.empty();
            }
        };
    }

}
