package dev.breezes.settlements.infrastructure.minecraft.data.traits;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DisplayInfoEntry {

    @SerializedName("display_name")
    String displayName;

    @SerializedName("description")
    String description;

    @SerializedName("custom_name")
    String customName;

    @SerializedName("icon_item_id")
    String iconItemId;

}
