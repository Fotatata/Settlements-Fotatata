package dev.breezes.settlements.domain.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BiomeIdTest {

    @Test
    void of_parsesNamespaceAndPathCorrectly() {
        BiomeId biomeId = BiomeId.of("minecraft:plains");

        assertEquals("minecraft", biomeId.namespace());
        assertEquals("plains", biomeId.path());
    }

    @Test
    void of_internsInstances() {
        assertSame(BiomeId.of("minecraft:plains"), BiomeId.of("minecraft:plains"));
    }

    @Test
    void of_missingColonThrows() {
        assertThrows(IllegalArgumentException.class, () -> BiomeId.of("minecraft_plains"));
    }

    @Test
    void full_returnsOriginalString() {
        assertEquals("minecraft:plains", BiomeId.of("minecraft:plains").full());
    }

}
