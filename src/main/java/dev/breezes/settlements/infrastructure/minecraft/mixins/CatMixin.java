package dev.breezes.settlements.infrastructure.minecraft.mixins;

import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.item.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import javax.annotation.Nonnull;

@Mixin(Cat.class)
public interface CatMixin {

    @Invoker("setCollarColor")
    void invokeSetCollarColor(@Nonnull DyeColor collarColor);

}
