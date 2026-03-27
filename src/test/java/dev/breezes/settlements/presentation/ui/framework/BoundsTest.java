package dev.breezes.settlements.presentation.ui.framework;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoundsTest {

    @Test
    void rightAndBottom() {
        Bounds b = new Bounds(10, 20, 100, 50);
        assertEquals(110, b.right());
        assertEquals(70, b.bottom());
    }

    @Test
    void containsInsidePoint() {
        Bounds b = new Bounds(10, 20, 100, 50);
        assertTrue(b.contains(50, 40));
    }

    @Test
    void containsTopLeftEdge() {
        Bounds b = new Bounds(10, 20, 100, 50);
        assertTrue(b.contains(10, 20));
    }

    @Test
    void containsExcludesRightAndBottomEdge() {
        Bounds b = new Bounds(10, 20, 100, 50);
        assertFalse(b.contains(110, 70)); // right, bottom
        assertFalse(b.contains(110, 40)); // right edge
        assertFalse(b.contains(50, 70));  // bottom edge
    }

    @Test
    void containsOutsidePoint() {
        Bounds b = new Bounds(10, 20, 100, 50);
        assertFalse(b.contains(5, 40));
        assertFalse(b.contains(50, 15));
    }

    @Test
    void zeroSizeBoundsContainsNothing() {
        assertFalse(Bounds.ZERO.contains(0, 0));
    }

}
