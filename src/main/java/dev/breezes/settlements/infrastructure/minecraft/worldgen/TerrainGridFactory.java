package dev.breezes.settlements.infrastructure.minecraft.worldgen;

import dev.breezes.settlements.domain.common.BiomeId;
import dev.breezes.settlements.domain.generation.model.geometry.BoundingRegion;
import dev.breezes.settlements.domain.generation.model.survey.SurveyBounds;
import dev.breezes.settlements.domain.generation.model.survey.TerrainGrid;
import dev.breezes.settlements.domain.generation.model.survey.TerrainSample;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;

import javax.annotation.Nonnull;

public final class TerrainGridFactory {

    public static TerrainGrid fromGenerationContext(@Nonnull Structure.GenerationContext context,
                                                    @Nonnull SurveyBounds bounds,
                                                    int sampleInterval) {
        ChunkGenerator chunkGenerator = context.chunkGenerator();
        return sampleGrid(bounds, sampleInterval, chunkGenerator,
                (worldX, worldZ) -> chunkGenerator.getBaseHeight(worldX, worldZ, Heightmap.Types.WORLD_SURFACE_WG,
                        context.heightAccessor(), context.randomState()),
                (worldX, worldZ) -> chunkGenerator.getBiomeSource().getNoiseBiome(worldX >> 2, 0, worldZ >> 2,
                        context.randomState().sampler()));
    }

    public static TerrainGrid fromServerLevel(@Nonnull ServerLevel level,
                                              @Nonnull SurveyBounds bounds,
                                              int sampleInterval) {
        ChunkGenerator chunkGenerator = level.getChunkSource().getGenerator();
        return sampleGrid(bounds, sampleInterval, chunkGenerator,
                (worldX, worldZ) -> chunkGenerator.getBaseHeight(worldX, worldZ, Heightmap.Types.WORLD_SURFACE_WG,
                        level, level.getChunkSource().randomState()),
                (worldX, worldZ) -> chunkGenerator.getBiomeSource().getNoiseBiome(worldX >> 2, 0, worldZ >> 2,
                        level.getChunkSource().randomState().sampler()));
    }

    private static TerrainGrid sampleGrid(@Nonnull SurveyBounds bounds,
                                          int sampleInterval,
                                          @Nonnull ChunkGenerator chunkGenerator,
                                          @Nonnull HeightSampler heightSampler,
                                          @Nonnull BiomeSampler biomeSampler) {
        BoundingRegion sampleArea = bounds.sampleArea();

        int gridWidth = (sampleArea.widthX() / sampleInterval) + 1;
        int gridDepth = (sampleArea.widthZ() / sampleInterval) + 1;
        TerrainSample[][] samples = new TerrainSample[gridDepth][gridWidth];

        for (int gz = 0; gz < gridDepth; gz++) {
            int worldZ = sampleArea.min().z() + gz * sampleInterval;
            for (int gx = 0; gx < gridWidth; gx++) {
                int worldX = sampleArea.min().x() + gx * sampleInterval;

                int height = heightSampler.sample(worldX, worldZ);

                Holder<Biome> biome = biomeSampler.sample(worldX, worldZ);
                BiomeId biomeId = BiomeId.of(biome.unwrapKey().map(key -> key.location().toString()).orElse("minecraft:plains"));

                float temperature = biome.value().getBaseTemperature();
                samples[gz][gx] = new TerrainSample(height, biomeId, temperature);
            }
        }

        return TerrainGrid.of(sampleArea.min().x(), sampleArea.min().z(), sampleInterval, samples);
    }

    @FunctionalInterface
    private interface HeightSampler {
        int sample(int worldX, int worldZ);
    }

    @FunctionalInterface
    private interface BiomeSampler {
        Holder<Biome> sample(int worldX, int worldZ);
    }

}
