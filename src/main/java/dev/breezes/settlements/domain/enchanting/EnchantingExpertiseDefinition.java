package dev.breezes.settlements.domain.enchanting;

import dev.breezes.settlements.domain.entities.Expertise;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.Nonnull;

@AllArgsConstructor
@Getter
public enum EnchantingExpertiseDefinition {

    NOVICE(60, 1),
    APPRENTICE(130, 1),
    JOURNEYMAN(220, 2),
    EXPERT(350, 2),
    MASTER(560, 3);

    private final int basePower;
    private final int maxRolls;

    public static EnchantingExpertiseDefinition of(@Nonnull Expertise expertise) {
        return switch (expertise) {
            case NOVICE -> NOVICE;
            case APPRENTICE -> APPRENTICE;
            case JOURNEYMAN -> JOURNEYMAN;
            case EXPERT -> EXPERT;
            case MASTER -> MASTER;
        };
    }

}
