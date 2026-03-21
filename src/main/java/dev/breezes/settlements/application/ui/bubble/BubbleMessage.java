package dev.breezes.settlements.application.ui.bubble;

import dev.breezes.settlements.domain.time.Ticks;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

/**
 * Immutable payload describing a single bubble to display for a villager.
 */
@Builder
@Getter
public final class BubbleMessage {

    /**
     * The bubble content variant to render.
     */
    private final BubbleKind bubbleKind;

    /**
     * Relative importance of the message within the same render channel.
     * <p>
     * Higher values represent higher priority. The default is 0.
     */
    @Builder.Default
    private final int priority = 0;

    /**
     * How long the bubble should remain visible before expiring.
     */
    private final Ticks ttl;

    /**
     * Stable identifier describing the subsystem or feature that produced the bubble.
     */
    private final String sourceType;

    /**
     * Optional renderer-facing metadata for this message.
     * <p>
     * Defaults to an immutable empty map when not explicitly provided.
     */
    @Builder.Default
    private final Map<String, String> extraData = Map.of();

}
