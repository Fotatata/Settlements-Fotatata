package dev.breezes.settlements.presentation.ui.framework;

import dev.breezes.settlements.shared.annotations.functional.ClientSide;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.gui.GuiGraphics;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Layers children at the same position (Z-stack).
 * First child = bottom, last child = top.
 * <p>
 * Use case: overlays, toasts, tutorials, unavailable-state dimming.
 * Conditional overlays: use a null-check in the overlay child's {@code render()},
 * if the condition is false, render is a no-op.
 */
@ClientSide
public class StackLayout extends BaseElement {

    private final List<UIElement> children;

    private StackLayout(@Nonnull SizeConstraint widthConstraint,
                        @Nonnull SizeConstraint heightConstraint,
                        @Nonnull Insets padding,
                        @Nonnull List<UIElement> children) {
        super(widthConstraint, heightConstraint, padding);
        this.children = List.copyOf(children);
    }

    @Override
    public List<UIElement> children() {
        return this.children;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void measure(int availableWidth, int availableHeight) {
        int innerW = availableWidth - this.padding.horizontalTotal();
        int innerH = availableHeight - this.padding.verticalTotal();

        int maxChildW = 0;
        int maxChildH = 0;

        for (UIElement child : this.children) {
            child.measure(innerW, innerH);
            maxChildW = Math.max(maxChildW, child.measuredWidth());
            maxChildH = Math.max(maxChildH, child.measuredHeight());
        }

        int resolvedW = resolveSize(this.widthConstraint, availableWidth, maxChildW + this.padding.horizontalTotal());
        int resolvedH = resolveSize(this.heightConstraint, availableHeight, maxChildH + this.padding.verticalTotal());
        setMeasuredSize(resolvedW, resolvedH);
    }

    @Override
    public void layout(@Nonnull Bounds bounds) {
        super.layout(bounds);

        Bounds innerBounds = new Bounds(
                bounds.x() + this.padding.left(),
                bounds.y() + this.padding.top(),
                bounds.width() - this.padding.horizontalTotal(),
                bounds.height() - this.padding.verticalTotal()
        );

        for (UIElement child : this.children) {
            child.layout(innerBounds);
        }
    }

    @Override
    public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        for (UIElement child : this.children) {
            child.render(graphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public void renderOverlay(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        for (UIElement child : this.children) {
            child.renderOverlay(graphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public void tick() {
        for (UIElement child : this.children) {
            child.tick();
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        // Iterate in reverse so the top-most child gets the first chance
        for (int i = this.children.size() - 1; i >= 0; i--) {
            UIElement child = this.children.get(i);
            if (child.bounds().contains((int) mouseX, (int) mouseY)
                    && child.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (int i = this.children.size() - 1; i >= 0; i--) {
            UIElement child = this.children.get(i);
            if (child.bounds().contains((int) mouseX, (int) mouseY)
                    && child.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Builder {

        private SizeConstraint width = SizeConstraint.FILL;
        private SizeConstraint height = SizeConstraint.FILL;
        private Insets padding = Insets.NONE;
        private final List<UIElement> children = new ArrayList<>();

        public Builder width(@Nonnull SizeConstraint width) {
            this.width = width;
            return this;
        }

        public Builder height(@Nonnull SizeConstraint height) {
            this.height = height;
            return this;
        }

        public Builder padding(@Nonnull Insets padding) {
            this.padding = padding;
            return this;
        }

        public Builder child(@Nonnull UIElement child) {
            this.children.add(child);
            return this;
        }

        public StackLayout build() {
            return new StackLayout(width, height, padding, children);
        }

    }

}
