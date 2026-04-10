package dev.breezes.settlements.infrastructure.minecraft.data.scoring;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
@Builder
public class TraitScorerEntry {

    @SerializedName("trait")
    String trait;

    @SerializedName("base_score")
    Float baseScore;

    @SerializedName("resource_tag_weights")
    Map<String, Float> resourceTagWeights;

    @SerializedName("required_tags")
    List<String> requiredTags;

    @SerializedName("veto_tags")
    List<String> vetoTags;

    @SerializedName("water_feature_weights")
    Map<String, Float> waterFeatureWeights;

    @SerializedName("biome_weights")
    Map<String, Float> biomeWeights;

    @SerializedName("elevation_delta_weight")
    Float elevationDeltaWeight;

    @SerializedName("elevation_delta_normalization")
    Float elevationDeltaNormalization;

}
