package dev.breezes.settlements.infrastructure.minecraft.worldgen;

import dev.breezes.settlements.domain.generation.model.geometry.BlockPosition;
import net.minecraft.core.BlockPos;

import javax.annotation.Nonnull;

public final class BlockPositionConverter {

    public static BlockPos toMinecraft(@Nonnull BlockPosition position) {
        return new BlockPos(position.x(), position.y(), position.z());
    }

}
