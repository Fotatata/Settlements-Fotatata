package dev.breezes.settlements.infrastructure.rendering.bubbles.registry;

import dev.breezes.settlements.application.ui.bubble.BubbleEntrySnapshot;
import dev.breezes.settlements.domain.time.Ticks;
import dev.breezes.settlements.infrastructure.rendering.bubbles.canvas.SpeechBubble;
import lombok.CustomLog;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;

@CustomLog
public class BubbleRegistry {

    private static final int DEFAULT_VISIBILITY_BLOCKS = 32;

    public static Optional<SpeechBubble> getBubble(@Nonnull BubbleEntrySnapshot snapshot, long currentGameTime) {
        try {
            UUID bubbleId = snapshot.bubbleId();
            Ticks remainingLifetime = Ticks.of(Math.max(1, snapshot.expireGameTime() - currentGameTime));

            return switch (snapshot.bubbleKind()) {
                case TEST_ONLY -> Optional.empty();
                case SHEAR_SHEEP ->
                        Optional.of(new ShearSheepSpeechBubble(bubbleId, DEFAULT_VISIBILITY_BLOCKS, remainingLifetime));
                case EMOTE_EXCITED ->
                        Optional.of(new EmoteExcitedSpeechBubble(bubbleId, DEFAULT_VISIBILITY_BLOCKS, remainingLifetime));
            };
        } catch (Exception e) {
            log.error("Failed to generate bubble from snapshot: {}", snapshot, e);
            return Optional.empty();
        }
    }

}
