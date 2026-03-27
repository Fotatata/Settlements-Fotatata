package dev.breezes.settlements.presentation.ui.framework;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScrollableListTest {

    private ScrollableList createList(int rowHeight, int rowCount) {
        ScrollableList list = ScrollableList.builder()
                .rowHeight(rowHeight)
                .rowFactory(() -> IntStream.range(0, rowCount)
                        .mapToObj(i -> (UIElement) TestElement.fixed(100, rowHeight))
                        .toList())
                .width(SizeConstraint.fixed(200))
                .height(SizeConstraint.fixed(100))
                .build();

        list.measure(200, 100);
        list.layout(new Bounds(0, 0, 200, 100));
        list.rebuildRows();
        return list;
    }

    @Test
    void maxVisibleRowsCalculation() {
        ScrollableList list = createList(25, 10);
        // 100px height / 25px row = 4 visible rows
        assertEquals(4, list.getMaxVisibleRows());
    }

    @Test
    void maxScrollOffsetWhenAllRowsFit() {
        ScrollableList list = createList(25, 3);
        // 3 rows fit in 4-row viewport
        assertEquals(0, list.getMaxScrollOffset());
    }

    @Test
    void maxScrollOffsetWhenOverflow() {
        ScrollableList list = createList(25, 10);
        // 10 rows, 4 visible -> max offset = 6
        assertEquals(6, list.getMaxScrollOffset());
    }

    @Test
    void rebuildRowsClampsScrollOffset() {
        // Start with many rows and scroll down
        ScrollableList list = ScrollableList.builder()
                .rowHeight(25)
                .rowFactory(() -> IntStream.range(0, 10)
                        .mapToObj(i -> (UIElement) TestElement.fixed(100, 25))
                        .toList())
                .width(SizeConstraint.fixed(200))
                .height(SizeConstraint.fixed(100))
                .build();

        list.measure(200, 100);
        list.layout(new Bounds(0, 0, 200, 100));
        list.rebuildRows();

        // Scroll to max
        for (int i = 0; i < 10; i++) {
            list.mouseScrolled(100, 50, 0, -1);
        }
        assertEquals(6, list.getScrollOffset());

        // Now rebuild with fewer rows (only 2) — should clamp scroll to 0
        ScrollableList list2 = ScrollableList.builder()
                .rowHeight(25)
                .rowFactory(() -> IntStream.range(0, 2)
                        .mapToObj(i -> (UIElement) TestElement.fixed(100, 25))
                        .toList())
                .width(SizeConstraint.fixed(200))
                .height(SizeConstraint.fixed(100))
                .build();

        list2.measure(200, 100);
        list2.layout(new Bounds(0, 0, 200, 100));
        list2.rebuildRows();
        assertEquals(0, list2.getScrollOffset());
    }

    @Test
    void onlyVisibleRowsGetLaidOut() {
        ScrollableList list = createList(25, 10);

        // First 4 rows (indices 0-3) should have non-zero bounds
        List<UIElement> rows = list.getRows();
        for (int i = 0; i < 4; i++) {
            assertEquals(25, rows.get(i).bounds().height(),
                    "Visible row " + i + " should have height 25");
        }

        // Rows beyond visible range should have zero bounds (not laid out)
        for (int i = 4; i < 10; i++) {
            assertEquals(Bounds.ZERO, rows.get(i).bounds(),
                    "Non-visible row " + i + " should have zero bounds");
        }
    }

    @Test
    void maxVisibleRowsWithPadding() {
        ScrollableList list = ScrollableList.builder()
                .rowHeight(25)
                .rowFactory(() -> IntStream.range(0, 10)
                        .mapToObj(i -> (UIElement) TestElement.fixed(100, 25))
                        .toList())
                .width(SizeConstraint.fixed(200))
                .height(SizeConstraint.fixed(100))
                .padding(Insets.symmetric(10, 0))
                .build();

        list.measure(200, 100);
        list.layout(new Bounds(0, 0, 200, 100));
        list.rebuildRows();

        // 100px - 20px padding = 80px / 25px = 3 visible rows
        assertEquals(3, list.getMaxVisibleRows());
    }

}
