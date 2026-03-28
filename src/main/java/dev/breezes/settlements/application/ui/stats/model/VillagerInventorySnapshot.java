package dev.breezes.settlements.application.ui.stats.model;

import lombok.Builder;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

@Builder
public record VillagerInventorySnapshot(
        int backpackSize,
        @Nonnull List<ItemStack> nonEmptyItems
) {

    public VillagerInventorySnapshot {
        nonEmptyItems = List.copyOf(nonEmptyItems);
    }

}
