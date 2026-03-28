package dev.breezes.settlements.infrastructure.network.features.ui.stats.packet;

import dev.breezes.settlements.infrastructure.network.core.ClientBoundPacket;
import dev.breezes.settlements.shared.util.ResourceLocationUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import javax.annotation.Nonnull;

public record ClientBoundHeartbeatAckVillagerStatsPacket(long sessionId) implements ClientBoundPacket {

    public static final Type<ClientBoundHeartbeatAckVillagerStatsPacket> ID =
            new Type<>(ResourceLocationUtil.mod("packet_villager_stats_heartbeat_ack_clientbound"));

    public static final StreamCodec<FriendlyByteBuf, ClientBoundHeartbeatAckVillagerStatsPacket> CODEC =
            CustomPacketPayload.codec(ClientBoundHeartbeatAckVillagerStatsPacket::write,
                    ClientBoundHeartbeatAckVillagerStatsPacket::decode);

    @Nonnull
    private static ClientBoundHeartbeatAckVillagerStatsPacket decode(@Nonnull FriendlyByteBuf buffer) {
        return new ClientBoundHeartbeatAckVillagerStatsPacket(buffer.readLong());
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
