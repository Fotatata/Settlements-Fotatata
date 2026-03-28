package dev.breezes.settlements.infrastructure.network.features.ui.stats.handler;

import dev.breezes.settlements.application.ui.stats.session.VillagerStatsSession;
import dev.breezes.settlements.application.ui.stats.session.VillagerStatsSessionService;
import dev.breezes.settlements.infrastructure.network.core.ServerSidePacketHandler;
import dev.breezes.settlements.infrastructure.network.core.annotations.HandleServerPacket;
import dev.breezes.settlements.infrastructure.network.features.ui.stats.packet.ServerBoundCloseVillagerStatsPacket;
import lombok.CustomLog;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nonnull;

@CustomLog
@HandleServerPacket(ServerBoundCloseVillagerStatsPacket.class)
public class ServerBoundCloseVillagerStatsPacketHandler implements ServerSidePacketHandler<ServerBoundCloseVillagerStatsPacket> {

    @Override
    public void runOnServer(@Nonnull IPayloadContext context, @Nonnull ServerBoundCloseVillagerStatsPacket packet) {
        if (!(context.player() instanceof ServerPlayer player)) {
            return;
        }

        VillagerStatsSessionService sessions = VillagerStatsSessionService.getInstance();
        boolean staleCloseRequest = sessions.isSessionStale(player.getUUID(), packet.sessionId());

        if (staleCloseRequest) {
            log.debug("Ignoring close for stale villager stats sessionId={} player={} activeSessionId={}",
                    packet.sessionId(), player.getUUID(),
                    sessions.getSession(player.getUUID()).map(VillagerStatsSession::getSessionId).orElse(-1L));
            return;
        }

        sessions.closeSession(player.getUUID(), packet.sessionId());

        log.debug("Closed villager stats sessionId={} for player={}", packet.sessionId(), player.getUUID());
    }

}
