package dev.breezes.settlements.domain.generation.scoring;

import dev.breezes.settlements.domain.common.BiomeId;
import dev.breezes.settlements.domain.generation.model.geometry.BlockPosition;
import dev.breezes.settlements.domain.generation.model.geometry.BoundingRegion;
import dev.breezes.settlements.domain.generation.model.profile.TraitId;
import dev.breezes.settlements.domain.generation.model.survey.ElevationStats;
import dev.breezes.settlements.domain.generation.model.survey.ResourceTag;
import dev.breezes.settlements.domain.generation.model.survey.SiteReport;
import dev.breezes.settlements.domain.generation.model.survey.SurveyBounds;
import dev.breezes.settlements.domain.generation.model.survey.TerrainGrid;
import dev.breezes.settlements.domain.generation.model.survey.TerrainSample;
import dev.breezes.settlements.domain.generation.model.survey.WaterFeatureType;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfiguredTraitScorerTest {

    private static final TraitId FARMING = TraitId.of("settlements:settlement_traits/farming");
    private static final TraitId FISHING = TraitId.of("settlements:settlement_traits/fishing");
    private static final TraitId MINING = TraitId.of("settlements:settlement_traits/mining");
    private static final TraitId DEFENSE = TraitId.of("settlements:settlement_traits/defense");

    @Test
    void vetoTag_returnsZeroImmediately() {
        ConfiguredTraitScorer scorer = new ConfiguredTraitScorer(new TraitScorerConfig(
                FARMING,
                0.4f,
                Map.of(ResourceTag.FRESHWATER, 0.2f),
                Set.of(),
                Set.of(ResourceTag.FROZEN),
                Map.of(),
                Map.of(),
                0.0f,
                0.0f
        ));

        assertEquals(0.0f, scorer.score(report(Set.of(ResourceTag.FROZEN), Map.of(), Map.of(), Set.of(), 0, 0)), 0.0001f);
    }

    @Test
    void missingRequiredTag_returnsZeroImmediately() {
        ConfiguredTraitScorer scorer = new ConfiguredTraitScorer(new TraitScorerConfig(
                FISHING,
                0.1f,
                Map.of(),
                Set.of(ResourceTag.FRESHWATER),
                Set.of(),
                Map.of(WaterFeatureType.RIVER, 0.3f),
                Map.of(),
                0.0f,
                0.0f
        ));

        assertEquals(0.0f, scorer.score(report(Set.of(), Map.of(), Map.of(), Set.of(WaterFeatureType.RIVER), 0, 0)), 0.0001f);
    }

    @Test
    void formula_appliesAllSignals() {
        ConfiguredTraitScorer scorer = new ConfiguredTraitScorer(new TraitScorerConfig(
                MINING,
                0.1f,
                Map.of(ResourceTag.STONE, 0.4f),
                Set.of(),
                Set.of(),
                Map.of(WaterFeatureType.LAKE, 0.2f),
                Map.of(BiomeId.of("minecraft:stony_peaks"), 0.3f),
                0.5f,
                60.0f
        ));

        SiteReport report = report(
                Set.of(ResourceTag.STONE),
                Map.of(ResourceTag.STONE, 0.5f),
                Map.of(BiomeId.of("minecraft:stony_peaks"), 0.25f),
                Set.of(WaterFeatureType.LAKE),
                60,
                120
        );

        assertEquals(1.0f, scorer.score(report), 0.0001f);
    }

    @Test
    void negativeWeight_reducesScore() {
        ConfiguredTraitScorer scorer = new ConfiguredTraitScorer(new TraitScorerConfig(
                FARMING,
                0.5f,
                Map.of(ResourceTag.LUMBER, -0.4f),
                Set.of(),
                Set.of(),
                Map.of(),
                Map.of(),
                0.0f,
                0.0f
        ));

        assertEquals(0.18f, scorer.score(report(Set.of(ResourceTag.LUMBER), Map.of(ResourceTag.LUMBER, 0.8f), Map.of(), Set.of(), 64, 64)), 0.0001f);
    }

    @Test
    void clamp_limitsRangeToZeroAndOne() {
        ConfiguredTraitScorer high = new ConfiguredTraitScorer(new TraitScorerConfig(
                DEFENSE,
                2.0f,
                Map.of(),
                Set.of(),
                Set.of(),
                Map.of(),
                Map.of(),
                0.0f,
                0.0f
        ));
        ConfiguredTraitScorer low = new ConfiguredTraitScorer(new TraitScorerConfig(
                DEFENSE,
                -1.0f,
                Map.of(),
                Set.of(),
                Set.of(),
                Map.of(),
                Map.of(),
                0.0f,
                0.0f
        ));

        SiteReport report = report(Set.of(), Map.of(), Map.of(), Set.of(), 64, 64);
        assertEquals(1.0f, high.score(report), 0.0001f);
        assertEquals(0.0f, low.score(report), 0.0001f);
    }

    private static SiteReport report(Set<ResourceTag> resourceTags,
                                     Map<ResourceTag, Float> resourceDensities,
                                     Map<BiomeId, Float> biomeDistribution,
                                     Set<WaterFeatureType> waterFeatureTypes,
                                     int minHeight,
                                     int maxHeight) {
        return new SiteReport(
                SurveyBounds.fromBuildArea(BoundingRegion.of(new BlockPosition(0, 64, 0), new BlockPosition(4, 64, 4)), 0),
                TerrainGrid.of(0, 0, 1, new TerrainSample[][]{{new TerrainSample(minHeight, BiomeId.of("minecraft:plains"), 0.8f)}}),
                new ElevationStats(minHeight, maxHeight, (minHeight + maxHeight) / 2, new BlockPosition(0, maxHeight, 0)),
                resourceDensities,
                biomeDistribution,
                waterFeatureTypes,
                resourceTags
        );
    }

}
