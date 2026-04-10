package dev.breezes.settlements.infrastructure.minecraft.data.traits;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TraitDefinitionEntry {

    @SerializedName("id")
    String id;

    @SerializedName("display_info")
    DisplayInfoEntry displayInfo;

}
