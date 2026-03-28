package dev.breezes.settlements.presentation.ui.stats;

import dev.breezes.settlements.shared.util.StringUtil;

import javax.annotation.Nonnull;

/**
 * Pure utility methods for villager stats rendering — extracted from VillagerStatsScreen
 * to allow unit testing without Minecraft class loading dependencies.
 */
final class VillagerStatsUtil {

    private static final String REPUTATION_HOSTILE_KEY = "ui.settlements.stats.reputation.hostile";
    private static final String REPUTATION_UNFRIENDLY_KEY = "ui.settlements.stats.reputation.unfriendly";
    private static final String REPUTATION_NEUTRAL_KEY = "ui.settlements.stats.reputation.neutral";
    private static final String REPUTATION_FRIENDLY_KEY = "ui.settlements.stats.reputation.friendly";
    private static final String REPUTATION_HONORED_KEY = "ui.settlements.stats.reputation.honored";
    private static final String REPUTATION_EXALTED_KEY = "ui.settlements.stats.reputation.exalted";

    static boolean isUnemployed(@Nonnull String professionKey) {
        String suffix = professionKeySuffix(professionKey);
        return "none".equals(suffix) || "nitwit".equals(suffix);
    }

    /**
     * Extracts the local name after the namespace colon (e.g., {@code "minecraft:farmer"} → {@code "farmer"}).
     * Returns the full key unchanged if no colon is present.
     */
    static String professionKeySuffix(@Nonnull String professionKey) {
        int colonIndex = professionKey.lastIndexOf(':');
        return colonIndex >= 0 ? professionKey.substring(colonIndex + 1) : professionKey;
    }

    static String formatProfessionName(@Nonnull String professionKey) {
        return StringUtil.titleCase(professionKeySuffix(professionKey).replace('_', ' '));
    }

    // TODO: move this out
    static String getReputationTitleKey(int reputation) {
        if (reputation <= -50) return REPUTATION_HOSTILE_KEY;
        if (reputation <= -10) return REPUTATION_UNFRIENDLY_KEY;
        if (reputation < 10) return REPUTATION_NEUTRAL_KEY;
        if (reputation < 50) return REPUTATION_FRIENDLY_KEY;
        if (reputation < 100) return REPUTATION_HONORED_KEY;
        return REPUTATION_EXALTED_KEY;
    }

}
