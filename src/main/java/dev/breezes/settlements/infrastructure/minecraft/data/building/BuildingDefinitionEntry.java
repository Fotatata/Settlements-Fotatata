package dev.breezes.settlements.infrastructure.minecraft.data.building;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
@Builder
public class BuildingDefinitionEntry {

    @SerializedName("id")
    String id;

    @SerializedName("placement_priority")
    Integer placementPriority;

    @SerializedName("zone_tier_min")
    Integer zoneTierMin;

    @SerializedName("zone_tier_max")
    Integer zoneTierMax;

    @SerializedName("requires_road_frontage")
    Boolean requiresRoadFrontage;

    @SerializedName("requires_resources")
    List<String> requiresResources;

    @SerializedName("forbidden_resources")
    List<String> forbiddenResources;

    @SerializedName("trait_affinities")
    Map<String, Float> traitAffinities;

    @SerializedName("minimum_rank")
    String minimumRank;

    @SerializedName("preferred_tags")
    List<String> preferredTags;

    @SerializedName("footprint_min_width")
    Integer footprintMinWidth;

    @SerializedName("footprint_max_width")
    Integer footprintMaxWidth;

    @SerializedName("footprint_min_depth")
    Integer footprintMinDepth;

    @SerializedName("footprint_max_depth")
    Integer footprintMaxDepth;

    @SerializedName("npc_profession")
    String npcProfession;

    @SerializedName("npc_count")
    Integer npcCount;

}
