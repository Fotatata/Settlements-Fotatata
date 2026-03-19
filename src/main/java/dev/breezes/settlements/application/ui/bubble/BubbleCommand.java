package dev.breezes.settlements.application.ui.bubble;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * Producer-facing command contract for mutating villager bubbles.
 */
public sealed interface BubbleCommand
        permits BubbleCommand.Upsert,
        BubbleCommand.Push,
        BubbleCommand.RemoveById,
        BubbleCommand.RemoveByOwner,
        BubbleCommand.ClearChannel {

    record Upsert(@Nonnull BubbleChannel channel,
            @Nonnull String ownerKey,
            @Nonnull BubbleMessage message) implements BubbleCommand {

        public Upsert {
            if (ownerKey.isBlank()) {
                throw new IllegalArgumentException("ownerKey must not be blank");
            }
        }

    }

    record Push(@Nonnull BubbleChannel channel,
            @Nonnull BubbleMessage message) implements BubbleCommand {

    }

    record RemoveById(@Nonnull UUID bubbleId) implements BubbleCommand {

    }

    record RemoveByOwner(@Nonnull BubbleChannel channel,
            @Nonnull String ownerKey) implements BubbleCommand {

        public RemoveByOwner {
            if (ownerKey.isBlank()) {
                throw new IllegalArgumentException("ownerKey must not be blank");
            }
        }
    }

    record ClearChannel(@Nonnull BubbleChannel channel) implements BubbleCommand {

    }

}
