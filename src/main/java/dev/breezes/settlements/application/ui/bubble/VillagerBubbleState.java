package dev.breezes.settlements.application.ui.bubble;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Authoritative per-villager bubble runtime state.
 */
public class VillagerBubbleState {

    private final Map<UUID, BubbleEntry> entriesById;
    private final Map<BubbleChannel, LinkedHashMap<UUID, BubbleEntry>> entriesByChannel;
    private final Map<BubbleChannel, Map<String, UUID>> ownerKeyIndexByChannel;
    private long nextSequenceNumber;

    public VillagerBubbleState() {
        this.entriesById = new LinkedHashMap<>();
        this.entriesByChannel = new EnumMap<>(BubbleChannel.class);
        this.ownerKeyIndexByChannel = new EnumMap<>(BubbleChannel.class);
        this.nextSequenceNumber = 0L;

        for (BubbleChannel channel : BubbleChannel.values()) {
            this.entriesByChannel.put(channel, new LinkedHashMap<>());
            this.ownerKeyIndexByChannel.put(channel, new LinkedHashMap<>());
        }
    }

    public long nextSequenceNumber() {
        return this.nextSequenceNumber++;
    }

    public List<BubbleEntry> getAllEntries() {
        return List.copyOf(this.entriesById.values());
    }

    public List<BubbleEntry> getEntries(@Nonnull BubbleChannel channel) {
        Collection<BubbleEntry> values = this.entriesByChannel.get(channel).values();
        return List.copyOf(values);
    }

    public int getChannelSize(@Nonnull BubbleChannel channel) {
        return this.entriesByChannel.get(channel).size();
    }

    public Optional<BubbleEntry> getById(@Nonnull UUID bubbleId) {
        return Optional.ofNullable(this.entriesById.get(bubbleId));
    }

    public Optional<BubbleEntry> getByOwner(@Nonnull BubbleChannel channel, @Nonnull String ownerKey) {
        UUID bubbleId = this.ownerKeyIndexByChannel.get(channel).get(ownerKey);
        return bubbleId == null ? Optional.empty() : this.getById(bubbleId);
    }

    public void put(@Nonnull BubbleEntry entry) {
        this.removeById(entry.bubbleId());
        if (entry.ownerKey() != null) {
            this.getByOwner(entry.channel(), entry.ownerKey())
                    .filter(existing -> !existing.bubbleId().equals(entry.bubbleId()))
                    .ifPresent(existing -> this.removeById(existing.bubbleId()));
        }

        this.entriesById.put(entry.bubbleId(), entry);
        this.entriesByChannel.get(entry.channel()).put(entry.bubbleId(), entry);
        if (entry.ownerKey() != null) {
            this.ownerKeyIndexByChannel.get(entry.channel()).put(entry.ownerKey(), entry.bubbleId());
        }
    }

    public Optional<BubbleEntry> removeById(@Nonnull UUID bubbleId) {
        BubbleEntry removed = this.entriesById.remove(bubbleId);
        if (removed == null) {
            return Optional.empty();
        }

        this.entriesByChannel.get(removed.channel()).remove(removed.bubbleId());
        if (removed.ownerKey() != null) {
            this.ownerKeyIndexByChannel.get(removed.channel()).remove(removed.ownerKey());
        }
        return Optional.of(removed);
    }

    public Optional<BubbleEntry> removeByOwner(@Nonnull BubbleChannel channel, @Nonnull String ownerKey) {
        UUID bubbleId = this.ownerKeyIndexByChannel.get(channel).get(ownerKey);
        return bubbleId == null ? Optional.empty() : this.removeById(bubbleId);
    }

    public List<BubbleEntry> clearChannel(@Nonnull BubbleChannel channel) {
        List<BubbleEntry> removedEntries = new ArrayList<>(this.entriesByChannel.get(channel).values());
        removedEntries.forEach(entry -> this.removeById(entry.bubbleId()));
        return List.copyOf(removedEntries);
    }

    public List<BubbleEntry> pruneExpired(long gameTime) {
        // Fast path: avoid any allocations if nothing is expired.
        boolean anyExpired = false;
        for (BubbleEntry entry : this.entriesById.values()) {
            if (entry.isExpiredAt(gameTime)) {
                anyExpired = true;
                break;
            }
        }
        if (!anyExpired) {
            return List.of();
        }

        // Slow path: collect IDs first to avoid concurrent modification while iterating.
        List<UUID> expiredIds = new ArrayList<>();
        for (BubbleEntry entry : this.entriesById.values()) {
            if (entry.isExpiredAt(gameTime)) {
                expiredIds.add(entry.bubbleId());
            }
        }
        if (expiredIds.isEmpty()) {
            return List.of();
        }

        List<BubbleEntry> removedEntries = new ArrayList<>(expiredIds.size());
        for (UUID expiredId : expiredIds) {
            this.removeById(expiredId).ifPresent(removedEntries::add);
        }
        return List.copyOf(removedEntries);
    }

}
