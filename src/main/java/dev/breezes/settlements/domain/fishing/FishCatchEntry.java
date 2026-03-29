package dev.breezes.settlements.domain.fishing;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class FishCatchEntry {

    @SerializedName("entity")
    String entityId;

    @SerializedName("item")
    String itemId;

    @SerializedName("weight")
    double weight;

}
