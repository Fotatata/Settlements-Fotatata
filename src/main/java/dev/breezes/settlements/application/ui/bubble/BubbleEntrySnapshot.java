package dev.breezes.settlements.application.ui.bubble;

import lombok.Builder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

/**
 * Replicated bubble snapshot entry sent from the authoritative server runtime to clients.
 */
@Builder
public record BubbleEntrySnapshot(
        @Nonnull UUID bubbleId,
        @Nonnull BubbleChannel channel,
        @Nonnull BubbleKind bubbleKind,
        @Nullable String ownerKey,
        int priority,
        long expireGameTime,
        long createdGameTime,
        long sequenceNumber,
        @Nonnull String sourceType,
        @Nonnull Map<String, String> extraData
) {

    public BubbleEntrySnapshot {
        extraData = Map.copyOf(extraData);
        if (createdGameTime < 0) {
            throw new IllegalArgumentException("createdGameTime must be >= 0");
        }
        if (expireGameTime <= createdGameTime) {
            throw new IllegalArgumentException("expireGameTime must be > createdGameTime");
        }
        if (sequenceNumber < 0) {
            throw new IllegalArgumentException("sequenceNumber must be >= 0");
        }
        if (ownerKey != null && ownerKey.isBlank()) {
            throw new IllegalArgumentException("ownerKey must not be blank");
        }
        if (sourceType.isBlank()) {
            throw new IllegalArgumentException("sourceType must not be blank");
        }
    }

    public static BubbleEntrySnapshot fromEntry(@Nonnull BubbleEntry entry) {
        return BubbleEntrySnapshot.builder()
                .bubbleId(entry.bubbleId())
                .channel(entry.channel())
                .bubbleKind(entry.message().getBubbleKind())
                .ownerKey(entry.ownerKey())
                .priority(entry.message().getPriority())
                .expireGameTime(entry.expireGameTime())
                .createdGameTime(entry.createdGameTime())
                .sequenceNumber(entry.sequenceNumber())
                .sourceType(entry.message().getSourceType())
                .extraData(entry.message().getExtraData())
                .build();
    }

}
