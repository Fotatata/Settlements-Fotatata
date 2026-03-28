package dev.breezes.settlements.infrastructure.network.features.ui.stats.handler;

import dev.breezes.settlements.infrastructure.network.core.ClientSidePacketHandler;
import dev.breezes.settlements.infrastructure.network.core.annotations.HandleClientPacket;
import dev.breezes.settlements.infrastructure.network.features.ui.stats.packet.ClientBoundHeartbeatAckVillagerStatsPacket;
import dev.breezes.settlements.presentation.ui.stats.VillagerStatsClientState;
import lombok.CustomLog;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nonnull;

@CustomLog
@HandleClientPacket(ClientBoundHeartbeatAckVillagerStatsPacket.class)
public class ClientBoundHeartbeatAckVillagerStatsPacketHandler implements ClientSidePacketHandler<ClientBoundHeartbeatAckVillagerStatsPacket> {

    @Override
    public void runOnClient(@Nonnull IPayloadContext context, @Nonnull ClientBoundHeartbeatAckVillagerStatsPacket packet) {
        boolean applied = VillagerStatsClientState.recordHeartbeatAck(packet.sessionId());
        if (!applied) {
            log.debug("Ignoring stale {} sessionId={}", packet.getClass().getSimpleName(), packet.sessionId());
        }
    }

}
