package dev.breezes.settlements.application.ai.behavior.usecases.villager.smelting.blastore;

import lombok.Builder;
import lombok.Getter;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

@Builder
@Getter
public class BlastOreRecipe {

    private final Item input;
    private final Item output;

    @Builder.Default
    private final int inputCount = 1;
    @Builder.Default
    private final int outputCount = 1;

    public ItemStack createInputStack() {
        return new ItemStack(this.input, this.inputCount);
    }

    public ItemStack createOutputStack() {
        return new ItemStack(this.output, this.outputCount);
    }

}
