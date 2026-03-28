package dev.breezes.settlements.domain.genetics;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum GeneType {

    STRENGTH("STR", "ui.settlements.gene.strength.desc"),
    CONSTITUTION("CON", "ui.settlements.gene.constitution.desc"),
    AGILITY("AGI", "ui.settlements.gene.agility.desc"),
    INTELLIGENCE("INT", "ui.settlements.gene.intelligence.desc"),
    WILL("WIL", "ui.settlements.gene.will.desc"),
    CHARISMA("CHA", "ui.settlements.gene.charisma.desc"),
    ;

    /**
     * Cached result of {@link #values()}
     */
    public static final GeneType[] VALUES = values();

    private final String abbreviation;
    private final String descriptionKey;

}
