package dev.breezes.settlements.infrastructure.network.features.ui.stats.packet;

import dev.breezes.settlements.infrastructure.network.core.ServerBoundPacket;
import dev.breezes.settlements.shared.util.ResourceLocationUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import javax.annotation.Nonnull;

public record ServerBoundCloseVillagerStatsPacket(long sessionId) implements ServerBoundPacket {

    public static final Type<ServerBoundCloseVillagerStatsPacket> ID =
            new Type<>(ResourceLocationUtil.mod("packet_villager_stats_close_serverbound"));

    public static final StreamCodec<FriendlyByteBuf, ServerBoundCloseVillagerStatsPacket> CODEC =
            CustomPacketPayload.codec(ServerBoundCloseVillagerStatsPacket::write, ServerBoundCloseVillagerStatsPacket::decode);

    @Nonnull
    private static ServerBoundCloseVillagerStatsPacket decode(@Nonnull FriendlyByteBuf buffer) {
        return new ServerBoundCloseVillagerStatsPacket(buffer.readLong());
    }

    private void write(@Nonnull FriendlyByteBuf buffer) {
        buffer.writeLong(this.sessionId);
    }

    @Nonnull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

}
