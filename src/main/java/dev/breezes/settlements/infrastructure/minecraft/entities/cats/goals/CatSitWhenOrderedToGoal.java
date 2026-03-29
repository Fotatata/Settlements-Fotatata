package dev.breezes.settlements.infrastructure.minecraft.entities.cats.goals;

import dev.breezes.settlements.infrastructure.minecraft.entities.cats.SettlementsCat;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;

public class CatSitWhenOrderedToGoal extends SitWhenOrderedToGoal {

    private final SettlementsCat cat;

    public CatSitWhenOrderedToGoal(SettlementsCat cat) {
        super(cat);
        this.cat = cat;
    }

    @Override
    public boolean canUse() {
        if (this.cat.isInWaterOrBubble() || !this.cat.onGround()) {
            return false;
        }
        return this.cat.isOrderedToSit();
    }

}
