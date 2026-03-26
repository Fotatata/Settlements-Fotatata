package dev.breezes.settlements.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.Nonnull;

@AllArgsConstructor
@Getter
public enum Expertise {

    NOVICE(1),
    APPRENTICE(2),
    JOURNEYMAN(3),
    EXPERT(4),
    MASTER(5);

    private final int level;

    public static Expertise fromLevel(int level) {
        for (Expertise expertise : Expertise.values()) {
            if (expertise.getLevel() == level) {
                return expertise;
            }
        }
        throw new IllegalArgumentException("Invalid level: " + level);
    }

    public static Expertise fromString(@Nonnull String level) {
        for (Expertise expertise : Expertise.values()) {
            if (expertise.name().equalsIgnoreCase(level)) {
                return expertise;
            }
        }
        throw new IllegalArgumentException("Invalid level: " + level);
    }

    public String getConfigName() {
        return this.name().toLowerCase();
    }

}
