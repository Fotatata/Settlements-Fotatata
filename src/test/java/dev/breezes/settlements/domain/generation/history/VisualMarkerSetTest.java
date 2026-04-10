package dev.breezes.settlements.domain.generation.history;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VisualMarkerSetTest {

    @Test
    void emptyConstant_isEmpty() {
        assertTrue(VisualMarkerSet.EMPTY.isEmpty());
        assertTrue(VisualMarkerSet.EMPTY.markers().isEmpty());
    }

    @Test
    void merge_unionsMarkers() {
        VisualMarkerSet first = new VisualMarkerSet(Set.of("charred", "taiga"));
        VisualMarkerSet second = new VisualMarkerSet(Set.of("taiga", "rebuilt"));

        VisualMarkerSet merged = first.merge(second);

        assertEquals(Set.of("charred", "taiga", "rebuilt"), merged.markers());
    }

    @Test
    void contains_checksMembership() {
        VisualMarkerSet set = new VisualMarkerSet(Set.of("charred"));

        assertTrue(set.contains("charred"));
        assertFalse(set.contains("snowy"));
    }

}
