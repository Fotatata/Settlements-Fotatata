package dev.breezes.settlements.application.ui.bubble;

import lombok.Builder;

import javax.annotation.Nonnull;
import dev.breezes.settlements.domain.time.Ticks;

@Builder
public record ChannelPolicy(
        int maxActive,
        @Nonnull OverflowPolicy overflowPolicy,
        int renderOrder,
        @Nonnull Ticks defaultTtlCap
) {

    public ChannelPolicy {
        if (maxActive <= 0) {
            throw new IllegalArgumentException("maxActive must be > 0");
        }
    }

    public Ticks clampTtl(@Nonnull Ticks requestedTtl) {
        return Ticks.of(Math.min(requestedTtl.getTicks(), this.defaultTtlCap.getTicks()));
    }

    public enum OverflowPolicy {
        /**
         * Always replace the existing active entry (used for single-slot channels).
         */
        REPLACE_EXISTING,
        /**
         * Drop the oldest entry in the channel when capacity is exceeded.
         */
        DROP_OLDEST,
        /**
         * Drop the lowest-priority entry, then oldest, then lowest sequence number.
         */
        DROP_LOWEST_PRIORITY,
        /**
         * Reject the incoming bubble without mutating existing entries.
         */
        REJECT_NEW
    }

}
