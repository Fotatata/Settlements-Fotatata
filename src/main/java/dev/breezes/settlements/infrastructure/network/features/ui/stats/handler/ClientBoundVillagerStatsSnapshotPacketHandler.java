package dev.breezes.settlements.infrastructure.network.features.ui.stats.handler;

import dev.breezes.settlements.infrastructure.network.core.ClientSidePacketHandler;
import dev.breezes.settlements.infrastructure.network.core.annotations.HandleClientPacket;
import dev.breezes.settlements.infrastructure.network.features.ui.stats.packet.ClientBoundVillagerStatsSnapshotPacket;
import dev.breezes.settlements.presentation.ui.stats.VillagerStatsClientState;
import dev.breezes.settlements.presentation.ui.stats.VillagerStatsScreen;
import lombok.CustomLog;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nonnull;

@CustomLog
@HandleClientPacket(ClientBoundVillagerStatsSnapshotPacket.class)
public class ClientBoundVillagerStatsSnapshotPacketHandler implements ClientSidePacketHandler<ClientBoundVillagerStatsSnapshotPacket> {

    @Override
    public void runOnClient(@Nonnull IPayloadContext context, @Nonnull ClientBoundVillagerStatsSnapshotPacket packet) {
        boolean applied = VillagerStatsClientState.applyStatsSnapshot(packet.sessionId(), packet.snapshot());
        if (!applied) {
            log.debug("Ignoring stale {} sessionId={}", packet.getClass().getSimpleName(), packet.sessionId());
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        Screen activeScreen = minecraft.screen;
        if (!(activeScreen instanceof VillagerStatsScreen statsScreen)) {
            log.debug("Cannot apply stats snapshot because client does not have a villager stats screen");
            return;
        }

        if (statsScreen.getSessionId() != packet.sessionId()) {
            log.debug("Cannot apply stats snapshot because session ID mismatch: expected {}, got {}", packet.sessionId(), statsScreen.getSessionId());
            return;
        }

        statsScreen.applyStatsSnapshot(packet.snapshot());
    }

}
