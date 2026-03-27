package dev.breezes.settlements.presentation.ui.framework;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StackLayoutTest {

    @Test
    void measuresToMaxChildSize() {
        TestElement small = TestElement.wrap(50, 30);
        TestElement large = TestElement.wrap(120, 80);

        StackLayout stack = StackLayout.builder()
                .width(SizeConstraint.WRAP)
                .height(SizeConstraint.WRAP)
                .child(small)
                .child(large)
                .build();

        stack.measure(300, 300);

        assertEquals(120, stack.measuredWidth());
        assertEquals(80, stack.measuredHeight());
    }

    @Test
    void allChildrenGetSameBounds() {
        TestElement a = TestElement.fill();
        TestElement b = TestElement.fill();
        TestElement c = TestElement.fill();

        StackLayout stack = StackLayout.builder()
                .width(SizeConstraint.fixed(200))
                .height(SizeConstraint.fixed(100))
                .child(a).child(b).child(c)
                .build();

        stack.measure(200, 100);
        stack.layout(new Bounds(10, 20, 200, 100));

        // All children should have the same bounds (inner bounds)
        assertEquals(a.bounds(), b.bounds());
        assertEquals(b.bounds(), c.bounds());
        assertEquals(10, a.bounds().x());
        assertEquals(20, a.bounds().y());
        assertEquals(200, a.bounds().width());
        assertEquals(100, a.bounds().height());
    }

    @Test
    void paddingReducesInnerBounds() {
        TestElement child = TestElement.fill();

        StackLayout stack = StackLayout.builder()
                .width(SizeConstraint.fixed(200))
                .height(SizeConstraint.fixed(100))
                .padding(Insets.uniform(10))
                .child(child)
                .build();

        stack.measure(200, 100);
        stack.layout(new Bounds(0, 0, 200, 100));

        assertEquals(10, child.bounds().x());
        assertEquals(10, child.bounds().y());
        assertEquals(180, child.bounds().width());
        assertEquals(80, child.bounds().height());
    }

}
