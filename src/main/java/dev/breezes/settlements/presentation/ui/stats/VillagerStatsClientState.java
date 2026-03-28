package dev.breezes.settlements.presentation.ui.stats;

import dev.breezes.settlements.application.ui.stats.model.VillagerInventorySnapshot;
import dev.breezes.settlements.application.ui.stats.model.VillagerStatsSnapshot;
import dev.breezes.settlements.infrastructure.network.features.ui.stats.packet.ServerBoundHeartbeatVillagerStatsPacket;
import dev.breezes.settlements.shared.annotations.functional.ClientSide;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Tracks the client-side state of an active villager stats session.
 * <p>
 * All access must occur on the Minecraft client thread (render/tick).
 * Not thread-safe by design — Minecraft's client is single-threaded.
 * <p>
 * Implemented as a singleton with instance state to allow test injection.
 * Static delegate methods preserve the existing call-site API.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ClientSide
public final class VillagerStatsClientState {

    private static final int HEARTBEAT_INTERVAL_TICKS = 40;
    private static final int SNAPSHOT_STALE_THRESHOLD_TICKS = 20;
    private static final int HEARTBEAT_ACK_STALE_THRESHOLD_TICKS = 120;

    private static final VillagerStatsClientState INSTANCE = new VillagerStatsClientState();

    private long activeSessionId = -1L;
    private int activeVillagerEntityId = -1;

    @Nullable
    private VillagerStatsSnapshot latestStatsSnapshot;
    @Nullable
    private VillagerInventorySnapshot latestInventorySnapshot;
    @Nullable
    private String unavailableReasonKey;
    private boolean sessionTerminalUnavailable = false;
    private long nextHeartbeatGameTime = 0L;
    private long lastSnapshotReceivedGameTime = 0L;
    private long lastHeartbeatAckReceivedGameTime = 0L;
    private int snapshotReceiveCount = 0;

    public static VillagerStatsClientState getInstance() {
        return INSTANCE;
    }

    // ---- Static delegates (preserve existing call-site API) ----

    public static void openSession(long sessionId, int villagerEntityId) {
        INSTANCE.doOpenSession(sessionId, villagerEntityId);
    }

    public static boolean applyStatsSnapshot(long sessionId, @Nonnull VillagerStatsSnapshot snapshot) {
        return INSTANCE.doApplyStatsSnapshot(sessionId, snapshot);
    }

    public static boolean applyInventorySnapshot(long sessionId, @Nonnull VillagerInventorySnapshot inventory) {
        return INSTANCE.doApplyInventorySnapshot(sessionId, inventory);
    }

    public static boolean recordHeartbeatAck(long sessionId) {
        return INSTANCE.doRecordHeartbeatAck(sessionId);
    }

    public static boolean markUnavailable(long sessionId, @Nonnull String reasonKey) {
        return INSTANCE.doMarkUnavailable(sessionId, reasonKey);
    }

    public static void clearSession(long sessionId) {
        INSTANCE.doClearSession(sessionId);
    }

    /**
     * Unconditionally resets all session state regardless of session ID.
     * Used by defensive cleanup to guarantee the state is cleared even on ID mismatch.
     */
    public static void forceClearSession() {
        INSTANCE.doForceClear();
    }

    public static boolean hasActiveSession() {
        return INSTANCE.activeSessionId > 0;
    }

    public static long activeSessionId() {
        return INSTANCE.activeSessionId;
    }

    public static int activeVillagerEntityId() {
        return INSTANCE.activeVillagerEntityId;
    }

    public static Optional<VillagerStatsSnapshot> latestStatsSnapshot() {
        return Optional.ofNullable(INSTANCE.latestStatsSnapshot);
    }

    public static Optional<VillagerInventorySnapshot> latestInventorySnapshot() {
        return Optional.ofNullable(INSTANCE.latestInventorySnapshot);
    }

    public static boolean isSnapshotUpdateStale(long screenSessionId) {
        return INSTANCE.doIsSnapshotUpdateStale(screenSessionId);
    }

    public static boolean isHeartbeatAckStale(long screenSessionId) {
        return INSTANCE.doIsHeartbeatAckStale(screenSessionId);
    }

    public static void tickHeartbeatIfNeeded(long screenSessionId) {
        INSTANCE.doTickHeartbeatIfNeeded(screenSessionId);
    }

    // ---- Instance methods ----

    private void doOpenSession(long sessionId, int villagerEntityId) {
        this.activeSessionId = sessionId;
        this.activeVillagerEntityId = villagerEntityId;
        this.latestStatsSnapshot = null;
        this.latestInventorySnapshot = null;
        this.unavailableReasonKey = null;
        this.sessionTerminalUnavailable = false;
        this.nextHeartbeatGameTime = 0L;
        this.lastSnapshotReceivedGameTime = 0L;
        this.lastHeartbeatAckReceivedGameTime = 0L;
        this.snapshotReceiveCount = 0;
    }

    private boolean doApplyStatsSnapshot(long sessionId, @Nonnull VillagerStatsSnapshot snapshot) {
        if (sessionId != this.activeSessionId) {
            return false;
        }

        this.latestStatsSnapshot = snapshot;
        this.unavailableReasonKey = null;
        Minecraft minecraft = Minecraft.getInstance();
        long observedGameTime = minecraft.level != null ? minecraft.level.getGameTime() : snapshot.gameTime();
        this.lastSnapshotReceivedGameTime = observedGameTime;
        this.lastHeartbeatAckReceivedGameTime = observedGameTime;
        this.snapshotReceiveCount++;
        return true;
    }

    private boolean doApplyInventorySnapshot(long sessionId, @Nonnull VillagerInventorySnapshot inventory) {
        if (sessionId != this.activeSessionId) {
            return false;
        }

        this.latestInventorySnapshot = inventory;
        return true;
    }

    private boolean doRecordHeartbeatAck(long sessionId) {
        if (sessionId != this.activeSessionId) {
            return false;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null) {
            this.lastHeartbeatAckReceivedGameTime = minecraft.level.getGameTime();
            return true;
        }

        if (this.latestStatsSnapshot != null) {
            this.lastHeartbeatAckReceivedGameTime = this.latestStatsSnapshot.gameTime();
            return true;
        }

        return false;
    }

    private boolean doMarkUnavailable(long sessionId, @Nonnull String reasonKey) {
        if (sessionId != this.activeSessionId) {
            return false;
        }

        this.unavailableReasonKey = reasonKey;
        this.sessionTerminalUnavailable = true;
        return true;
    }

    private void doClearSession(long sessionId) {
        if (sessionId != this.activeSessionId) {
            return;
        }
        doForceClear();
    }

    private void doForceClear() {
        this.activeSessionId = -1L;
        this.activeVillagerEntityId = -1;
        this.latestStatsSnapshot = null;
        this.latestInventorySnapshot = null;
        this.unavailableReasonKey = null;
        this.sessionTerminalUnavailable = false;
        this.nextHeartbeatGameTime = 0L;
        this.lastSnapshotReceivedGameTime = 0L;
        this.lastHeartbeatAckReceivedGameTime = 0L;
        this.snapshotReceiveCount = 0;
    }

    private boolean doIsSnapshotUpdateStale(long screenSessionId) {
        if (screenSessionId <= 0 || screenSessionId != this.activeSessionId) {
            return false;
        }

        if (this.snapshotReceiveCount < 2) {
            return false;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return false;
        }

        long gameTime = minecraft.level.getGameTime();
        return (gameTime - this.lastSnapshotReceivedGameTime) > SNAPSHOT_STALE_THRESHOLD_TICKS;
    }

    private boolean doIsHeartbeatAckStale(long screenSessionId) {
        if (screenSessionId <= 0 || screenSessionId != this.activeSessionId) {
            return false;
        }

        if (this.lastHeartbeatAckReceivedGameTime <= 0L) {
            return false;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return false;
        }

        long gameTime = minecraft.level.getGameTime();
        return (gameTime - this.lastHeartbeatAckReceivedGameTime) > HEARTBEAT_ACK_STALE_THRESHOLD_TICKS;
    }

    private void doTickHeartbeatIfNeeded(long screenSessionId) {
        if (screenSessionId <= 0 || screenSessionId != this.activeSessionId) {
            return;
        }

        if (this.sessionTerminalUnavailable) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            return;
        }

        long gameTime = minecraft.level.getGameTime();
        if (gameTime < this.nextHeartbeatGameTime) {
            return;
        }

        PacketDistributor.sendToServer(new ServerBoundHeartbeatVillagerStatsPacket(screenSessionId));
        this.nextHeartbeatGameTime = gameTime + HEARTBEAT_INTERVAL_TICKS;
    }

}
