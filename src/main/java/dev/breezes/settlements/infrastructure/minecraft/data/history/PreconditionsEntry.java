package dev.breezes.settlements.infrastructure.minecraft.data.history;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
@Builder
public class PreconditionsEntry {

    @SerializedName("min_trait_weights")
    Map<String, Float> minTraitWeights;

    @SerializedName("required_resource_tags")
    List<String> requiredResourceTags;

    @SerializedName("required_water_features")
    List<String> requiredWaterFeatures;

    @SerializedName("min_population")
    Integer minPopulation;

}
