package dev.breezes.settlements.presentation.ui.framework;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InsetsTest {

    @Test
    void horizontalAndVerticalTotal() {
        Insets insets = new Insets(5, 10, 15, 20);
        assertEquals(30, insets.horizontalTotal()); // left(20) + right(10)
        assertEquals(20, insets.verticalTotal());   // top(5) + bottom(15)
    }

    @Test
    void uniformFactory() {
        Insets insets = Insets.uniform(8);
        assertEquals(8, insets.top());
        assertEquals(8, insets.right());
        assertEquals(8, insets.bottom());
        assertEquals(8, insets.left());
    }

    @Test
    void symmetricFactory() {
        Insets insets = Insets.symmetric(4, 12);
        assertEquals(4, insets.top());
        assertEquals(12, insets.right());
        assertEquals(4, insets.bottom());
        assertEquals(12, insets.left());
    }

    @Test
    void noneIsAllZeros() {
        Insets none = Insets.NONE;
        assertEquals(0, none.horizontalTotal());
        assertEquals(0, none.verticalTotal());
    }

}
