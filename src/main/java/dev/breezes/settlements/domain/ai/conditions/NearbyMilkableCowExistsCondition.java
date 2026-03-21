package dev.breezes.settlements.domain.ai.conditions;

import dev.breezes.settlements.infrastructure.minecraft.entities.villager.BaseVillager;
import lombok.Builder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cow;

import javax.annotation.Nullable;

public class NearbyMilkableCowExistsCondition<T extends BaseVillager> extends NearbyEntityExistsCondition<T, Cow> {

    @Builder
    private NearbyMilkableCowExistsCondition(double rangeHorizontal, double rangeVertical) {
        super(rangeHorizontal, rangeVertical, EntityType.COW, NearbyMilkableCowExistsCondition::isMilkable, 1);
    }

    private static boolean isMilkable(@Nullable Cow cow) {
        return cow != null && cow.isAlive() && !cow.isBaby();
    }

}
