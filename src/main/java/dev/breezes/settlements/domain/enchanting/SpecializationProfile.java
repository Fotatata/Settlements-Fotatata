package dev.breezes.settlements.domain.enchanting;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Value;

import java.util.Map;

@Value
@Builder
public class SpecializationProfile {

    String id;

    @SerializedName("display_name")
    String displayName;

    @Builder.Default
    String description = "";

    @Builder.Default
    @SerializedName("enchantment_weights")
    Map<String, Float> enchantmentWeights = Map.of();

    public float getWeight(String enchantmentId) {
        if (this.enchantmentWeights == null) {
            return 1.0f;
        }
        return this.enchantmentWeights.getOrDefault(enchantmentId, 1.0f);
    }

}