package dev.breezes.settlements.bootstrap.event;

import dev.breezes.settlements.SettlementsMod;
import dev.breezes.settlements.application.ui.stats.model.VillagerInventorySnapshot;
import dev.breezes.settlements.application.ui.stats.model.VillagerStatsSnapshot;
import dev.breezes.settlements.application.ui.stats.session.VillagerStatsSession;
import dev.breezes.settlements.application.ui.stats.session.VillagerStatsSessionService;
import dev.breezes.settlements.application.ui.stats.snapshot.VillagerStatsSnapshotBuilder;
import dev.breezes.settlements.domain.time.Ticks;
import dev.breezes.settlements.infrastructure.minecraft.entities.villager.BaseVillager;
import dev.breezes.settlements.infrastructure.network.features.ui.stats.packet.ClientBoundVillagerInventorySnapshotPacket;
import dev.breezes.settlements.infrastructure.network.features.ui.stats.packet.ClientBoundVillagerStatsSnapshotPacket;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.CustomLog;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = SettlementsMod.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@CustomLog
public final class VillagerStatsServerEvents {

    private static final int STATS_SNAPSHOT_INTERVAL_TICKS = Ticks.seconds(0.5).getTicksAsInt();
    private static final int INVENTORY_SNAPSHOT_INTERVAL_TICKS = Ticks.seconds(3).getTicksAsInt();

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        long gameTime = event.getServer().overworld().getGameTime();

        boolean isStatsTick = gameTime % STATS_SNAPSHOT_INTERVAL_TICKS == 0;
        boolean isInventoryTick = gameTime % INVENTORY_SNAPSHOT_INTERVAL_TICKS == 0;
        if (!isStatsTick && !isInventoryTick) {
            return;
        }

        VillagerStatsSessionService sessions = VillagerStatsSessionService.getInstance();
        if (isStatsTick) {
            sessions.cleanupInvalidSessions(event.getServer(), gameTime);
        }

        VillagerStatsSnapshotBuilder builder = VillagerStatsSnapshotBuilder.getInstance();
        for (VillagerStatsSession session : sessions.getAllSessions()) {
            ServerPlayer player = event.getServer().getPlayerList().getPlayer(session.getPlayerUuid());
            if (player == null) {
                continue;
            }

            // cleanupInvalidSessions() already removed dead/removed/missing villagers above,
            // so this only needs the instanceof cast guard
            Entity entity = player.serverLevel().getEntity(session.getVillagerEntityId());
            if (!(entity instanceof BaseVillager villager)) {
                continue;
            }

            if (isStatsTick) {
                VillagerStatsSnapshot statsSnapshot = builder.buildStats(villager, gameTime);
                PacketDistributor.sendToPlayer(player,
                        new ClientBoundVillagerStatsSnapshotPacket(session.getSessionId(), statsSnapshot));
                session.markStatsSnapshotSent(gameTime);
            }

            if (isInventoryTick) {
                int currentInventoryVersion = villager.getSettlementsInventory().getInventoryVersion();
                if (currentInventoryVersion != session.getLastSentInventoryVersion()) {
                    VillagerInventorySnapshot inventorySnapshot = builder.buildInventory(villager);
                    PacketDistributor.sendToPlayer(player,
                            new ClientBoundVillagerInventorySnapshotPacket(session.getSessionId(), inventorySnapshot));
                    session.markInventorySnapshotSent(gameTime, currentInventoryVersion);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        VillagerStatsSessionService sessions = VillagerStatsSessionService.getInstance();
        sessions.getSession(event.getEntity().getUUID())
                .ifPresent(session -> sessions.closeSession(session.getPlayerUuid(), session.getSessionId()));
    }

}
