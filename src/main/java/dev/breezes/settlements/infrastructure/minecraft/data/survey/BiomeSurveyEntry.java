package dev.breezes.settlements.infrastructure.minecraft.data.survey;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
@Builder
public class BiomeSurveyEntry {

    @SerializedName("biome")
    String biome;

    @SerializedName("resource_densities")
    Map<String, Float> resourceDensities;

    @SerializedName("water_type")
    String waterType;

    @SerializedName("template_tags")
    List<String> templateTags;

}
