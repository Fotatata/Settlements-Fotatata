package dev.breezes.settlements.domain.generation.model.profile;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TraitIdTest {

    private static final TraitId FARMING = TraitId.of("settlements:settlement_traits/farming");

    @Test
    void repeatedLookupReturnsSameInstance() {
        TraitId first = TraitId.of("settlements:settlement_traits/lumber");
        TraitId second = TraitId.of("settlements:settlement_traits/lumber");

        assertSame(first, second);
    }

    @Test
    void namespaceAndPathParseCorrectly() {
        TraitId traitId = TraitId.of("settlements:settlement_traits/farming");

        assertEquals("settlements", traitId.namespace());
        assertEquals("settlement_traits/farming", traitId.path());
    }

    @Test
    void invalidValuesThrow() {
        assertThrows(IllegalArgumentException.class, () -> TraitId.of(null));
        assertThrows(IllegalArgumentException.class, () -> TraitId.of(""));
        assertThrows(IllegalArgumentException.class, () -> TraitId.of("lumber"));
        assertThrows(IllegalArgumentException.class, () -> TraitId.of(":lumber"));
        assertThrows(IllegalArgumentException.class, () -> TraitId.of("settlements:"));
    }

    @Test
    void toStringReturnsFullNamespacedId() {
        TraitId traitId = TraitId.of("settlements:settlement_traits/defense");

        assertEquals("settlements:settlement_traits/defense", traitId.toString());
    }

    @Test
    void identityComparisonWorksAcrossRepeatedLookup() {
        assertSame(FARMING, TraitId.of("settlements:settlement_traits/farming"));
    }

}
