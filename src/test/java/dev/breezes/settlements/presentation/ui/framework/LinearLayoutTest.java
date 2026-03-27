package dev.breezes.settlements.presentation.ui.framework;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LinearLayoutTest {

    private void measureAndLayout(UIElement element, int width, int height) {
        element.measure(width, height);
        element.layout(new Bounds(0, 0, element.measuredWidth(), element.measuredHeight()));
    }

    @Nested
    class VerticalLayout {

        @Test
        void stacksChildrenTopToBottom() {
            TestElement a = TestElement.fixed(100, 30);
            TestElement b = TestElement.fixed(100, 40);
            TestElement c = TestElement.fixed(100, 50);

            LinearLayout layout = LinearLayout.vertical()
                    .width(SizeConstraint.fixed(200))
                    .height(SizeConstraint.fixed(200))
                    .child(a).child(b).child(c)
                    .build();

            measureAndLayout(layout, 200, 200);

            assertEquals(0, a.bounds().y());
            assertEquals(30, b.bounds().y());
            assertEquals(70, c.bounds().y());
        }

        @Test
        void childrenGetFullInnerWidth() {
            TestElement child = TestElement.fixed(50, 30);

            LinearLayout layout = LinearLayout.vertical()
                    .width(SizeConstraint.fixed(200))
                    .height(SizeConstraint.fixed(100))
                    .child(child)
                    .build();

            measureAndLayout(layout, 200, 100);

            // Child should get the full inner width (200), not its fixed 50
            assertEquals(200, child.bounds().width());
        }

        @Test
        void gapBetweenChildren() {
            TestElement a = TestElement.fixed(100, 20);
            TestElement b = TestElement.fixed(100, 20);
            TestElement c = TestElement.fixed(100, 20);

            LinearLayout layout = LinearLayout.vertical()
                    .width(SizeConstraint.fixed(100))
                    .height(SizeConstraint.fixed(200))
                    .gap(5)
                    .child(a).child(b).child(c)
                    .build();

            measureAndLayout(layout, 100, 200);

            assertEquals(0, a.bounds().y());
            assertEquals(25, b.bounds().y());  // 20 + 5 gap
            assertEquals(50, c.bounds().y());  // 25 + 20 + 5 gap
        }

        @Test
        void paddingOffsetsChildren() {
            TestElement child = TestElement.fixed(100, 30);

            LinearLayout layout = LinearLayout.vertical()
                    .width(SizeConstraint.fixed(200))
                    .height(SizeConstraint.fixed(100))
                    .padding(Insets.uniform(10))
                    .child(child)
                    .build();

            measureAndLayout(layout, 200, 100);

            assertEquals(10, child.bounds().x());
            assertEquals(10, child.bounds().y());
            assertEquals(180, child.bounds().width()); // 200 - 10*2
        }
    }

    @Nested
    class HorizontalLayout {

        @Test
        void stacksChildrenLeftToRight() {
            TestElement a = TestElement.fixed(30, 100);
            TestElement b = TestElement.fixed(40, 100);
            TestElement c = TestElement.fixed(50, 100);

            LinearLayout layout = LinearLayout.horizontal()
                    .width(SizeConstraint.fixed(200))
                    .height(SizeConstraint.fixed(100))
                    .child(a).child(b).child(c)
                    .build();

            measureAndLayout(layout, 200, 100);

            assertEquals(0, a.bounds().x());
            assertEquals(30, b.bounds().x());
            assertEquals(70, c.bounds().x());
        }

        @Test
        void gapBetweenChildren() {
            TestElement a = TestElement.fixed(30, 100);
            TestElement b = TestElement.fixed(30, 100);

            LinearLayout layout = LinearLayout.horizontal()
                    .width(SizeConstraint.fixed(200))
                    .height(SizeConstraint.fixed(100))
                    .gap(10)
                    .child(a).child(b)
                    .build();

            measureAndLayout(layout, 200, 100);

            assertEquals(0, a.bounds().x());
            assertEquals(40, b.bounds().x()); // 30 + 10 gap
        }
    }

    @Nested
    class WeightedDistribution {

        @Test
        void singleWeightedChildGetsAllRemainingSpace() {
            TestElement fixedChild = TestElement.fixed(100, 30);
            TestElement weightedChild = TestElement.weightedOnAxis(LinearLayout.Axis.VERTICAL, 1, 100);

            LinearLayout layout = LinearLayout.vertical()
                    .width(SizeConstraint.fixed(100))
                    .height(SizeConstraint.fixed(200))
                    .child(fixedChild)
                    .child(weightedChild)
                    .build();

            measureAndLayout(layout, 100, 200);

            assertEquals(30, fixedChild.bounds().height());
            assertEquals(170, weightedChild.bounds().height()); // 200 - 30
        }

        @Test
        void twoWeightedChildrenSplitProportionally() {
            TestElement w1 = TestElement.weightedOnAxis(LinearLayout.Axis.VERTICAL, 1, 100);
            TestElement w2 = TestElement.weightedOnAxis(LinearLayout.Axis.VERTICAL, 2, 100);

            LinearLayout layout = LinearLayout.vertical()
                    .width(SizeConstraint.fixed(100))
                    .height(SizeConstraint.fixed(300))
                    .child(w1)
                    .child(w2)
                    .build();

            measureAndLayout(layout, 100, 300);

            assertEquals(100, w1.bounds().height()); // 300 * 1/3
            assertEquals(200, w2.bounds().height()); // 300 * 2/3
        }

        @Test
        void fillTreatedAsWeightedOne() {
            TestElement fill1 = new TestElement(SizeConstraint.fixed(100), SizeConstraint.FILL, 100, 0);
            TestElement fill2 = new TestElement(SizeConstraint.fixed(100), SizeConstraint.FILL, 100, 0);

            LinearLayout layout = LinearLayout.vertical()
                    .width(SizeConstraint.fixed(100))
                    .height(SizeConstraint.fixed(200))
                    .child(fill1)
                    .child(fill2)
                    .build();

            measureAndLayout(layout, 100, 200);

            assertEquals(100, fill1.bounds().height());
            assertEquals(100, fill2.bounds().height());
        }

        @Test
        void zeroRemainingSpaceGivesWeightedChildrenZero() {
            TestElement fixedChild = TestElement.fixed(100, 200); // takes all space
            TestElement weightedChild = TestElement.weightedOnAxis(LinearLayout.Axis.VERTICAL, 1, 100);

            LinearLayout layout = LinearLayout.vertical()
                    .width(SizeConstraint.fixed(100))
                    .height(SizeConstraint.fixed(200))
                    .child(fixedChild)
                    .child(weightedChild)
                    .build();

            measureAndLayout(layout, 100, 200);

            assertEquals(200, fixedChild.bounds().height());
            assertEquals(0, weightedChild.bounds().height());
        }
    }

    @Nested
    class WrapSizing {

        @Test
        void wrapShrinksToCombinedChildHeight() {
            TestElement a = TestElement.fixed(50, 30);
            TestElement b = TestElement.fixed(50, 40);

            LinearLayout layout = LinearLayout.vertical()
                    .width(SizeConstraint.fixed(100))
                    .height(SizeConstraint.WRAP)
                    .child(a).child(b)
                    .build();

            layout.measure(300, 300);

            assertEquals(70, layout.measuredHeight()); // 30 + 40
        }

        @Test
        void wrapWithGapIncludesGaps() {
            TestElement a = TestElement.fixed(50, 20);
            TestElement b = TestElement.fixed(50, 20);
            TestElement c = TestElement.fixed(50, 20);

            LinearLayout layout = LinearLayout.vertical()
                    .width(SizeConstraint.fixed(100))
                    .height(SizeConstraint.WRAP)
                    .gap(5)
                    .child(a).child(b).child(c)
                    .build();

            layout.measure(300, 300);

            assertEquals(70, layout.measuredHeight()); // 3*20 + 2*5
        }

        @Test
        void wrapWithPaddingIncludesPadding() {
            TestElement child = TestElement.fixed(50, 30);

            LinearLayout layout = LinearLayout.vertical()
                    .width(SizeConstraint.WRAP)
                    .height(SizeConstraint.WRAP)
                    .padding(Insets.uniform(10))
                    .child(child)
                    .build();

            layout.measure(300, 300);

            assertEquals(70, layout.measuredWidth());  // 50 + 10*2
            assertEquals(50, layout.measuredHeight()); // 30 + 10*2
        }
    }

    @Nested
    class EdgeCases {

        @Test
        void emptyLayoutMeasuresToPaddingOnly() {
            LinearLayout layout = LinearLayout.vertical()
                    .width(SizeConstraint.WRAP)
                    .height(SizeConstraint.WRAP)
                    .padding(Insets.uniform(5))
                    .build();

            layout.measure(300, 300);

            assertEquals(10, layout.measuredWidth());
            assertEquals(10, layout.measuredHeight());
        }

        @Test
        void singleChildDegeneratesCorrectly() {
            TestElement child = TestElement.fixed(80, 40);

            LinearLayout layout = LinearLayout.vertical()
                    .width(SizeConstraint.fixed(100))
                    .height(SizeConstraint.fixed(100))
                    .child(child)
                    .build();

            measureAndLayout(layout, 100, 100);

            assertEquals(0, child.bounds().x());
            assertEquals(0, child.bounds().y());
            assertEquals(100, child.bounds().width());
            assertEquals(40, child.bounds().height());
        }

        @Test
        void nestedLayoutsResolveCorrectly() {
            TestElement innerChild = TestElement.fixed(50, 20);
            LinearLayout innerLayout = LinearLayout.vertical()
                    .width(SizeConstraint.FILL)
                    .height(SizeConstraint.WRAP)
                    .child(innerChild)
                    .build();

            TestElement outerChild = TestElement.fixed(100, 30);
            LinearLayout outerLayout = LinearLayout.vertical()
                    .width(SizeConstraint.fixed(200))
                    .height(SizeConstraint.fixed(200))
                    .child(outerChild)
                    .child(innerLayout)
                    .build();

            measureAndLayout(outerLayout, 200, 200);

            assertEquals(0, outerChild.bounds().y());
            assertEquals(30, innerLayout.bounds().y());
            assertEquals(200, innerLayout.bounds().width());

            // Inner child should be positioned relative to inner layout
            assertEquals(30, innerChild.bounds().y());
        }

        @Test
        void mixedFixedAndWeightedChildren() {
            TestElement fixed1 = TestElement.fixed(100, 50);
            TestElement weighted = TestElement.weightedOnAxis(LinearLayout.Axis.VERTICAL, 1, 100);
            TestElement fixed2 = TestElement.fixed(100, 30);

            LinearLayout layout = LinearLayout.vertical()
                    .width(SizeConstraint.fixed(100))
                    .height(SizeConstraint.fixed(200))
                    .child(fixed1)
                    .child(weighted)
                    .child(fixed2)
                    .build();

            measureAndLayout(layout, 100, 200);

            assertEquals(50, fixed1.bounds().height());
            assertEquals(120, weighted.bounds().height()); // 200 - 50 - 30
            assertEquals(30, fixed2.bounds().height());

            assertEquals(0, fixed1.bounds().y());
            assertEquals(50, weighted.bounds().y());
            assertEquals(170, fixed2.bounds().y());
        }
    }

}
