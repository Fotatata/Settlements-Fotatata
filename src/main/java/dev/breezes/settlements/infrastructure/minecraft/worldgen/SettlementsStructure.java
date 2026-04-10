package dev.breezes.settlements.infrastructure.minecraft.worldgen;

import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import javax.annotation.Nonnull;
import java.util.Optional;

public abstract class SettlementsStructure extends Structure {

    protected SettlementsStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(@Nonnull GenerationContext context) {
        return onTopOfChunkCenter(context, Heightmap.Types.WORLD_SURFACE_WG,
                structurePiecesBuilder -> this.generatePieces(structurePiecesBuilder, context));
    }

    protected abstract void generatePieces(StructurePiecesBuilder builder, GenerationContext context);

}
