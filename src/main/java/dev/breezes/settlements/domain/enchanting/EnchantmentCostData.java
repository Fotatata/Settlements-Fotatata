package dev.breezes.settlements.domain.enchanting;

import com.google.gson.annotations.SerializedName;
import dev.breezes.settlements.domain.entities.Expertise;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class EnchantmentCostData {

    @SerializedName("enchantment")
    String enchantmentId;

    @SerializedName("base_cost")
    int baseCost;

    @SerializedName("level_multiplier")
    int levelMultiplier;

    @SerializedName("max_level")
    int maxLevel;

    @SerializedName("min_tier")
    String minTier;

    public int costForLevel(int level) {
        return this.baseCost + ((level - 1) * this.levelMultiplier);
    }

    public int minTierOrdinal() {
        try {
            return Expertise.fromString(this.minTier).ordinal();
        } catch (IllegalArgumentException e) {
            return 0;
        }
    }

}