package dev.breezes.settlements.infrastructure.minecraft.worldgen.pieces;

import dev.breezes.settlements.bootstrap.registry.structures.StructureRegistry;
import dev.breezes.settlements.domain.generation.model.geometry.Direction;
import dev.breezes.settlements.infrastructure.minecraft.worldgen.SettlementsStructurePiece;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import javax.annotation.Nonnull;

public class SettlementBuildingPiece extends SettlementsStructurePiece {

    public SettlementBuildingPiece(StructureTemplateManager templateManager, ResourceLocation templateId,
                                   BlockPos position, Rotation rotation) {
        super(StructureRegistry.SETTLEMENT_BUILDING_PIECE_TYPE.get(), templateManager, templateId, position, rotation);
    }

    private SettlementBuildingPiece(StructureTemplateManager templateManager, CompoundTag tag) {
        super(StructureRegistry.SETTLEMENT_BUILDING_PIECE_TYPE.get(), tag, templateManager,
                resourceLocation -> SettlementsStructurePiece.createSettings(SettlementsStructurePiece.readRotation(tag)));
    }

    public static SettlementBuildingPiece deserialize(StructurePieceSerializationContext context, CompoundTag tag) {
        return new SettlementBuildingPiece(context.structureTemplateManager(), tag);
    }

    public static Rotation fromDirection(@Nonnull Direction direction) {
        return switch (direction) {
            case NORTH -> Rotation.NONE;
            case EAST -> Rotation.CLOCKWISE_90;
            case SOUTH -> Rotation.CLOCKWISE_180;
            case WEST -> Rotation.COUNTERCLOCKWISE_90;
        };
    }

}
