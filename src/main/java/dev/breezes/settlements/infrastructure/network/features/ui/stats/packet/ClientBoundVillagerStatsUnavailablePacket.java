package dev.breezes.settlements.infrastructure.network.features.ui.stats.packet;

import dev.breezes.settlements.infrastructure.network.core.ClientBoundPacket;
import dev.breezes.settlements.shared.util.ResourceLocationUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import javax.annotation.Nonnull;

public record ClientBoundVillagerStatsUnavailablePacket(long sessionId,
                                                         @Nonnull String reasonKey) implements ClientBoundPacket {

    private static final int MAX_TEXT_LENGTH = 256;

    public static final Type<ClientBoundVillagerStatsUnavailablePacket> ID =
            new Type<>(ResourceLocationUtil.mod("packet_villager_stats_unavailable_clientbound"));

    public static final StreamCodec<FriendlyByteBuf, ClientBoundVillagerStatsUnavailablePacket> CODEC =
            CustomPacketPayload.codec(ClientBoundVillagerStatsUnavailablePacket::write, ClientBoundVillagerStatsUnavailablePacket::decode);

    @Nonnull
    private static ClientBoundVillagerStatsUnavailablePacket decode(@Nonnull FriendlyByteBuf buffer) {
        return new ClientBoundVillagerStatsUnavailablePacket(buffer.readLong(), buffer.readUtf(MAX_TEXT_LENGTH));
    }

    private void write(@Nonnull FriendlyByteBuf buffer) {
        buffer.writeLong(this.sessionId);
        buffer.writeUtf(this.reasonKey, MAX_TEXT_LENGTH);
    }

    @Nonnull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

}
