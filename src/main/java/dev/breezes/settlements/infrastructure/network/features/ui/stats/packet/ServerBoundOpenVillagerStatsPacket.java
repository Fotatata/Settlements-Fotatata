package dev.breezes.settlements.infrastructure.network.features.ui.stats.packet;

import dev.breezes.settlements.infrastructure.network.core.ServerBoundPacket;
import dev.breezes.settlements.shared.util.ResourceLocationUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import javax.annotation.Nonnull;

public record ServerBoundOpenVillagerStatsPacket(int villagerEntityId) implements ServerBoundPacket {

    public static final Type<ServerBoundOpenVillagerStatsPacket> ID =
            new Type<>(ResourceLocationUtil.mod("packet_villager_stats_open_serverbound"));

    public static final StreamCodec<FriendlyByteBuf, ServerBoundOpenVillagerStatsPacket> CODEC =
            CustomPacketPayload.codec(ServerBoundOpenVillagerStatsPacket::write, ServerBoundOpenVillagerStatsPacket::decode);

    @Nonnull
    private static ServerBoundOpenVillagerStatsPacket decode(@Nonnull FriendlyByteBuf buffer) {
        return new ServerBoundOpenVillagerStatsPacket(buffer.readInt());
    }

    private void write(@Nonnull FriendlyByteBuf buffer) {
        buffer.writeInt(this.villagerEntityId);
    }

    @Nonnull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

}
