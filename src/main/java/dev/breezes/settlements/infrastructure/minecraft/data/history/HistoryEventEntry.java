package dev.breezes.settlements.infrastructure.minecraft.data.history;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
@Builder
public class HistoryEventEntry {

    @SerializedName("id")
    String id;

    @SerializedName("category")
    String category;

    @SerializedName("time_horizon_min")
    Integer timeHorizonMin;

    @SerializedName("time_horizon_max")
    Integer timeHorizonMax;

    @SerializedName("exclusive_tags")
    List<String> exclusiveTags;

    @SerializedName("probability_weight")
    Float probabilityWeight;

    @SerializedName("preconditions")
    PreconditionsEntry preconditions;

    @SerializedName("trait_modifiers")
    Map<String, Float> traitModifiers;

    @SerializedName("visual_markers")
    List<String> visualMarkers;

    @SerializedName("narrative_text")
    String narrativeText;

}
