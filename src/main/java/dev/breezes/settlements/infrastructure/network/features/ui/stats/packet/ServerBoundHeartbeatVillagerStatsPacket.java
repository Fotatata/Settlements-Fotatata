package dev.breezes.settlements.infrastructure.network.features.ui.stats.packet;

import dev.breezes.settlements.infrastructure.network.core.ServerBoundPacket;
import dev.breezes.settlements.shared.util.ResourceLocationUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import javax.annotation.Nonnull;

public record ServerBoundHeartbeatVillagerStatsPacket(long sessionId) implements ServerBoundPacket {

    public static final Type<ServerBoundHeartbeatVillagerStatsPacket> ID =
            new Type<>(ResourceLocationUtil.mod("packet_villager_stats_heartbeat_serverbound"));

    public static final StreamCodec<FriendlyByteBuf, ServerBoundHeartbeatVillagerStatsPacket> CODEC =
            CustomPacketPayload.codec(ServerBoundHeartbeatVillagerStatsPacket::write, ServerBoundHeartbeatVillagerStatsPacket::decode);

    @Nonnull
    private static ServerBoundHeartbeatVillagerStatsPacket decode(@Nonnull FriendlyByteBuf buffer) {
        return new ServerBoundHeartbeatVillagerStatsPacket(buffer.readLong());
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
