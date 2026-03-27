package dev.breezes.settlements.presentation.ui.framework;

import net.minecraft.client.gui.GuiGraphics;

import javax.annotation.Nonnull;

/**
 * Stub element for testing container layout logic without Minecraft dependencies.
 * Provides configurable intrinsic sizes so containers can be tested in isolation.
 */
class TestElement extends BaseElement {

    private final int intrinsicWidth;
    private final int intrinsicHeight;

    TestElement(@Nonnull SizeConstraint widthConstraint,
                @Nonnull SizeConstraint heightConstraint,
                int intrinsicWidth,
                int intrinsicHeight) {
        super(widthConstraint, heightConstraint, Insets.NONE);
        this.intrinsicWidth = intrinsicWidth;
        this.intrinsicHeight = intrinsicHeight;
    }

    static TestElement fixed(int width, int height) {
        return new TestElement(SizeConstraint.fixed(width), SizeConstraint.fixed(height), width, height);
    }

    static TestElement wrap(int intrinsicWidth, int intrinsicHeight) {
        return new TestElement(SizeConstraint.WRAP, SizeConstraint.WRAP, intrinsicWidth, intrinsicHeight);
    }

    static TestElement fill() {
        return new TestElement(SizeConstraint.FILL, SizeConstraint.FILL, 0, 0);
    }

    static TestElement weighted(int weight) {
        return new TestElement(SizeConstraint.weighted(weight), SizeConstraint.weighted(weight), 0, 0);
    }

    static TestElement weightedOnAxis(LinearLayout.Axis axis, int weight, int crossSize) {
        if (axis == LinearLayout.Axis.VERTICAL) {
            return new TestElement(SizeConstraint.fixed(crossSize), SizeConstraint.weighted(weight), crossSize, 0);
        } else {
            return new TestElement(SizeConstraint.weighted(weight), SizeConstraint.fixed(crossSize), 0, crossSize);
        }
    }

    @Override
    public void measure(int availableWidth, int availableHeight) {
        int w = resolveSize(widthConstraint, availableWidth, intrinsicWidth);
        int h = resolveSize(heightConstraint, availableHeight, intrinsicHeight);
        setMeasuredSize(w, h);
    }

    @Override
    public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // no-op in tests
    }

}
