package dev.breezes.settlements.application.ui.bubble;

import lombok.Builder;

import javax.annotation.Nonnull;
import dev.breezes.settlements.domain.time.Ticks;
import java.util.Map;

@Builder
public record BubbleMessage(
        @Nonnull BubbleKind bubbleKind,
        int priority,
        @Nonnull Ticks ttl,
        @Nonnull String sourceType,
        @Nonnull Map<String, String> extraData
) {

    public BubbleMessage {
        extraData = Map.copyOf(extraData);
    }

}
