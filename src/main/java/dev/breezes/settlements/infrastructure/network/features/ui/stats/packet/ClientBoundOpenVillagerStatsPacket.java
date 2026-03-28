package dev.breezes.settlements.infrastructure.network.features.ui.stats.packet;

import dev.breezes.settlements.infrastructure.network.core.ClientBoundPacket;
import dev.breezes.settlements.shared.util.ResourceLocationUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import javax.annotation.Nonnull;

public record ClientBoundOpenVillagerStatsPacket(long sessionId,
                                                  int villagerEntityId) implements ClientBoundPacket {

    public static final Type<ClientBoundOpenVillagerStatsPacket> ID =
            new Type<>(ResourceLocationUtil.mod("packet_villager_stats_open_clientbound"));

    public static final StreamCodec<FriendlyByteBuf, ClientBoundOpenVillagerStatsPacket> CODEC =
            CustomPacketPayload.codec(ClientBoundOpenVillagerStatsPacket::write, ClientBoundOpenVillagerStatsPacket::decode);

    @Nonnull
    private static ClientBoundOpenVillagerStatsPacket decode(@Nonnull FriendlyByteBuf buffer) {
        return new ClientBoundOpenVillagerStatsPacket(buffer.readLong(), buffer.readInt());
    }

    private void write(@Nonnull FriendlyByteBuf buffer) {
        buffer.writeLong(this.sessionId);
        buffer.writeInt(this.villagerEntityId);
    }

    @Nonnull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

}
