package dev.breezes.settlements.domain.generation.survey;

import dev.breezes.settlements.domain.common.BiomeId;
import dev.breezes.settlements.domain.generation.model.geometry.BlockPosition;
import dev.breezes.settlements.domain.generation.model.geometry.BoundingRegion;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SurveyEngineTest {

    private final SurveyEngine engine = new SurveyEngine(this::lookup);

    @Test
    void flatPlains_hasNoWaterAndNoLumberTag() {
        SiteReport report = this.engine.analyze(gridFilled(50, 50, 64, BiomeId.of("minecraft:plains")), boundsForGrid(50, 50));

        assertTrue(report.waterFeatureTypes().isEmpty());
        assertEquals(64, report.elevation().min());
        assertEquals(64, report.elevation().max());
        assertEquals(0.1f, report.resourceDensities().get(ResourceTag.LUMBER), 0.0001f);
        assertFalse(report.resourceTags().contains(ResourceTag.LUMBER));
    }

    @Test
    void riverCrossing_detectsRiverAndFreshwater() {
        TerrainSample[][] samples = filledSamples(50, 50, 64, BiomeId.of("minecraft:plains"));
        for (int z = 0; z < 50; z++) {
            for (int x = 23; x <= 25; x++) {
                samples[z][x] = new TerrainSample(62, BiomeId.of("minecraft:river"), 0.5f);
            }
        }

        SiteReport report = this.engine.analyze(TerrainGrid.of(0, 0, 1, samples), boundsForGrid(50, 50));

        assertTrue(report.resourceTags().contains(ResourceTag.FRESHWATER));
        assertTrue(report.waterFeatureTypes().contains(WaterFeatureType.RIVER));
    }

    @Test
    void coastal_detectsOceanAndCoastal() {
        TerrainSample[][] samples = new TerrainSample[50][50];
        for (int z = 0; z < 50; z++) {
            for (int x = 0; x < 50; x++) {
                if (x < 25) {
                    samples[z][x] = new TerrainSample(64, BiomeId.of("minecraft:plains"), 0.8f);
                } else {
                    samples[z][x] = new TerrainSample(45, BiomeId.of("minecraft:ocean"), 0.5f);
                }
            }
        }

        SiteReport report = this.engine.analyze(TerrainGrid.of(0, 0, 1, samples), boundsForGrid(50, 50));
        assertTrue(report.resourceTags().contains(ResourceTag.COASTAL));
        assertTrue(report.waterFeatureTypes().contains(WaterFeatureType.OCEAN));
    }

    @Test
    void denseForest_hasLumberDensity() {
        SiteReport report = this.engine.analyze(gridFilled(50, 50, 68, BiomeId.of("minecraft:dark_forest")), boundsForGrid(50, 50));

        assertTrue(report.resourceTags().contains(ResourceTag.LUMBER));
        assertEquals(0.9f, report.resourceDensities().get(ResourceTag.LUMBER), 0.0001f);
    }

    @Test
    void mountainous_hasStoneAndOreBearing() {
        TerrainSample[][] samples = new TerrainSample[50][50];
        for (int z = 0; z < 50; z++) {
            for (int x = 0; x < 50; x++) {
                samples[z][x] = new TerrainSample(60 + ((x + z) % 61), BiomeId.of("minecraft:stony_peaks"), 0.2f);
            }
        }

        SiteReport report = this.engine.analyze(TerrainGrid.of(0, 0, 1, samples), boundsForGrid(50, 50));
        assertTrue(report.resourceTags().contains(ResourceTag.STONE));
        assertTrue(report.resourceTags().contains(ResourceTag.ORE_BEARING));
        assertTrue(report.elevation().max() - report.elevation().min() >= 60);
    }

    @Test
    void lakeInterior_detectsLake() {
        TerrainSample[][] samples = filledSamples(30, 30, 64, BiomeId.of("minecraft:plains"));
        for (int z = 12; z < 17; z++) {
            for (int x = 12; x < 17; x++) {
                samples[z][x] = new TerrainSample(62, BiomeId.of("minecraft:river"), 0.5f);
            }
        }

        SiteReport report = this.engine.analyze(TerrainGrid.of(0, 0, 1, samples), boundsForGrid(30, 30));
        assertTrue(report.waterFeatureTypes().contains(WaterFeatureType.LAKE));
    }

    @Test
    void mixedBiome_aggregatesExpectedDensities() {
        TerrainSample[][] samples = new TerrainSample[10][10];
        int index = 0;
        for (int z = 0; z < 10; z++) {
            for (int x = 0; x < 10; x++) {
                if (index < 60) {
                    samples[z][x] = new TerrainSample(68, BiomeId.of("minecraft:forest"), 0.7f);
                } else if (index < 90) {
                    samples[z][x] = new TerrainSample(64, BiomeId.of("minecraft:plains"), 0.8f);
                } else {
                    samples[z][x] = new TerrainSample(62, BiomeId.of("minecraft:river"), 0.5f);
                }
                index++;
            }
        }

        SiteReport report = this.engine.analyze(TerrainGrid.of(0, 0, 1, samples), boundsForGrid(10, 10));
        assertEquals(0.45f, report.resourceDensities().get(ResourceTag.LUMBER), 0.0001f);
        assertTrue(report.resourceTags().contains(ResourceTag.LUMBER));
        assertTrue(report.resourceTags().contains(ResourceTag.FRESHWATER));
    }

    @Test
    void determinism_sameGridTwice_returnsEquivalentReport() {
        TerrainGrid grid = gridFilled(20, 20, 64, BiomeId.of("minecraft:plains"));
        SurveyBounds bounds = boundsForGrid(20, 20);

        SiteReport first = this.engine.analyze(grid, bounds);
        SiteReport second = this.engine.analyze(grid, bounds);

        assertEquals(first, second);
    }

    private BiomeSurveyData lookup(BiomeId biomeId) {
        return switch (biomeId.full()) {
            case "minecraft:plains" -> new BiomeSurveyData(Map.of(ResourceTag.LUMBER, 0.1f), null, Set.of());
            case "minecraft:forest" -> new BiomeSurveyData(Map.of(ResourceTag.LUMBER, 0.7f), null, Set.of());
            case "minecraft:dark_forest" -> new BiomeSurveyData(Map.of(ResourceTag.LUMBER, 0.9f), null, Set.of());
            case "minecraft:river" -> new BiomeSurveyData(Map.of(ResourceTag.FRESHWATER, 1.0f), WaterFeatureType.RIVER, Set.of());
            case "minecraft:ocean" -> new BiomeSurveyData(Map.of(ResourceTag.COASTAL, 1.0f), WaterFeatureType.OCEAN, Set.of());
            case "minecraft:stony_peaks" ->
                    new BiomeSurveyData(Map.of(ResourceTag.STONE, 0.8f, ResourceTag.ORE_BEARING, 0.6f), null, Set.of());
            default -> BiomeSurveyData.DEFAULT;
        };
    }

    private static TerrainGrid gridFilled(int width, int depth, int height, BiomeId biomeId) {
        return TerrainGrid.of(0, 0, 1, filledSamples(width, depth, height, biomeId));
    }

    private static TerrainSample[][] filledSamples(int width, int depth, int height, BiomeId biomeId) {
        TerrainSample[][] samples = new TerrainSample[depth][width];
        for (int z = 0; z < depth; z++) {
            for (int x = 0; x < width; x++) {
                samples[z][x] = new TerrainSample(height, biomeId, 0.8f);
            }
        }
        return samples;
    }

    private static SurveyBounds boundsForGrid(int width, int depth) {
        return new SurveyBounds(
                BoundingRegion.of(new BlockPosition(0, 0, 0), new BlockPosition(width - 1, 0, depth - 1)),
                BoundingRegion.of(new BlockPosition(0, 0, 0), new BlockPosition(width - 1, 0, depth - 1)),
                0
        );
    }

}
