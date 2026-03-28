package dev.breezes.settlements.presentation.ui.framework;

import dev.breezes.settlements.shared.annotations.functional.ClientSide;
import net.minecraft.client.gui.GuiGraphics;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Stacks children vertically or horizontally, distributing space
 * according to each child's {@link SizeConstraint}.
 *
 * <p>Uses a two-pass measure algorithm:</p>
 * <ol>
 *     <li>Measure non-weighted children, accumulate used space</li>
 *     <li>Distribute remaining space to {@link SizeConstraint.Weighted} and
 *         {@link SizeConstraint.Fill} children proportionally</li>
 * </ol>
 */
@ClientSide
public class LinearLayout extends BaseElement {

    public enum Axis {
        HORIZONTAL,
        VERTICAL
    }

    public enum CrossAxisAlignment {
        START,
        CENTER,
        END,
        STRETCH
    }

    private final Axis axis;
    private final CrossAxisAlignment crossAxisAlignment;
    private final int gap;
    private final int backgroundColor;
    private final List<UIElement> children;

    private LinearLayout(@Nonnull Axis axis,
                         @Nonnull CrossAxisAlignment crossAxisAlignment,
                         int gap,
                         @Nonnull SizeConstraint widthConstraint,
                         @Nonnull SizeConstraint heightConstraint,
                         @Nonnull Insets padding,
                         int backgroundColor,
                         @Nonnull List<UIElement> children) {
        super(widthConstraint, heightConstraint, padding);
        this.axis = axis;
        this.crossAxisAlignment = crossAxisAlignment;
        this.gap = gap;
        this.backgroundColor = backgroundColor;
        this.children = List.copyOf(children);
    }

    @Override
    public List<UIElement> children() {
        return this.children;
    }

    public static Builder vertical() {
        return new Builder(Axis.VERTICAL);
    }

    public static Builder horizontal() {
        return new Builder(Axis.HORIZONTAL);
    }

    @Override
    public void measure(int availableWidth, int availableHeight) {
        int innerW = availableWidth - this.padding.horizontalTotal();
        int innerH = availableHeight - this.padding.verticalTotal();

        int gapTotal = this.children.isEmpty() ? 0 : (this.children.size() - 1) * this.gap;
        int usedMain = 0;
        int maxCross = 0;
        int totalWeight = 0;

        // Pass 1: measure non-weighted children
        for (UIElement child : this.children) {
            int weight = getEffectiveWeight(child);
            if (weight > 0) {
                totalWeight += weight;
                continue;
            }

            child.measure(innerW, innerH);
            if (this.axis == Axis.VERTICAL) {
                usedMain += child.measuredHeight();
                maxCross = Math.max(maxCross, child.measuredWidth());
            } else {
                usedMain += child.measuredWidth();
                maxCross = Math.max(maxCross, child.measuredHeight());
            }
        }

        // Pass 2: distribute remaining space to weighted children
        int mainAvailable = (this.axis == Axis.VERTICAL ? innerH : innerW) - usedMain - gapTotal;
        mainAvailable = Math.max(0, mainAvailable);

        if (totalWeight > 0 && mainAvailable > 0) {
            int distributed = 0;
            int weightedCount = 0;
            int totalWeightedChildren = (int) this.children.stream().filter(c -> getEffectiveWeight(c) > 0).count();

            for (UIElement child : this.children) {
                int weight = getEffectiveWeight(child);
                if (weight <= 0) {
                    continue;
                }
                weightedCount++;

                int share;
                // Give the last weighted child whatever remains to avoid rounding errors
                if (weightedCount == totalWeightedChildren) {
                    share = mainAvailable - distributed;
                } else {
                    share = mainAvailable * weight / totalWeight;
                }

                // WeightedMax: clamp to its declared maximum
                share = clampShareToMax(child, share);
                distributed += share;

                if (this.axis == Axis.VERTICAL) {
                    child.measure(innerW, share);
                } else {
                    child.measure(share, innerH);
                }
                usedMain += (this.axis == Axis.VERTICAL) ? child.measuredHeight() : child.measuredWidth();
                maxCross = Math.max(maxCross, (this.axis == Axis.VERTICAL) ? child.measuredWidth() : child.measuredHeight());
            }
        } else if (totalWeight > 0) {
            // Zero remaining space — measure weighted children with 0
            for (UIElement child : this.children) {
                if (getEffectiveWeight(child) > 0) {
                    child.measure(this.axis == Axis.VERTICAL ? innerW : 0, this.axis == Axis.VERTICAL ? 0 : innerH);
                }
            }
        }

        int totalMain = usedMain + gapTotal;
        int resolvedW = resolveSize(this.widthConstraint, availableWidth,
                (this.axis == Axis.HORIZONTAL ? totalMain : maxCross) + this.padding.horizontalTotal());
        int resolvedH = resolveSize(this.heightConstraint, availableHeight,
                (this.axis == Axis.VERTICAL ? totalMain : maxCross) + this.padding.verticalTotal());
        setMeasuredSize(resolvedW, resolvedH);
    }

    @Override
    public void layout(@Nonnull Bounds bounds) {
        super.layout(bounds);

        int cx = bounds.x() + this.padding.left();
        int cy = bounds.y() + this.padding.top();
        int innerW = bounds.width() - this.padding.horizontalTotal();
        int innerH = bounds.height() - this.padding.verticalTotal();

        // Intentional re-measure: the parent may assign bounds different from what was estimated
        // during the initial measure pass (e.g., a Weighted child's share depends on final parent size).
        // This ensures children resolve with the actual available space, not stale estimates.
        // TODO: Revisit if tree depth exceeds ~15 levels.
        measure(bounds.width(), bounds.height());

        int pos = (this.axis == Axis.VERTICAL) ? cy : cx;
        for (UIElement child : children) {
            if (this.axis == Axis.VERTICAL) {
                int childW = resolveChildCrossSize(child.measuredWidth(), innerW);
                int childX = alignCrossAxis(cx, innerW, childW);
                child.layout(new Bounds(childX, pos, childW, child.measuredHeight()));
                pos += child.measuredHeight() + this.gap;
            } else {
                int childH = resolveChildCrossSize(child.measuredHeight(), innerH);
                int childY = alignCrossAxis(cy, innerH, childH);
                child.layout(new Bounds(pos, childY, child.measuredWidth(), childH));
                pos += child.measuredWidth() + this.gap;
            }
        }
    }

    @Override
    public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (this.backgroundColor != 0) {
            Bounds b = bounds();
            graphics.fill(b.x(), b.y(), b.right(), b.bottom(), this.backgroundColor);
        }
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
        for (UIElement child : this.children) {
            if (child.bounds().contains((int) mouseX, (int) mouseY)
                    && child.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (UIElement child : this.children) {
            if (child.bounds().contains((int) mouseX, (int) mouseY)
                    && child.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    private int resolveChildCrossSize(int measuredCross, int availableCross) {
        return this.crossAxisAlignment == CrossAxisAlignment.STRETCH ? availableCross : measuredCross;
    }

    private int alignCrossAxis(int crossStart, int availableCross, int childCross) {
        return switch (this.crossAxisAlignment) {
            case START, STRETCH -> crossStart;
            case CENTER -> crossStart + (availableCross - childCross) / 2;
            case END -> crossStart + availableCross - childCross;
        };
    }

    /**
     * Returns the effective weight for a child element.
     * {@link SizeConstraint.Fill} is treated as {@code Weighted(1)}.
     * Returns 0 for non-weighted constraints.
     */
    private int getEffectiveWeight(@Nonnull UIElement child) {
        SizeConstraint mainConstraint = (this.axis == Axis.VERTICAL)
                ? child.heightConstraint()
                : child.widthConstraint();

        return switch (mainConstraint) {
            case SizeConstraint.Weighted w -> w.weight();
            case SizeConstraint.WeightedMax wm -> wm.weight();
            case SizeConstraint.Fill ignored -> 1;
            default -> 0;
        };
    }

    /**
     * If the child has a {@link SizeConstraint.WeightedMax} constraint on the main axis,
     * clamps the computed share to its declared maximum.
     * Otherwise, returns {@code share} unchanged.
     */
    private int clampShareToMax(@Nonnull UIElement child, int share) {
        SizeConstraint mainConstraint = (this.axis == Axis.VERTICAL)
                ? child.heightConstraint()
                : child.widthConstraint();

        if (mainConstraint instanceof SizeConstraint.WeightedMax wm) {
            return Math.min(share, wm.maxPixels());
        }
        return share;
    }

    public static final class Builder {

        private final Axis axis;
        private CrossAxisAlignment crossAxisAlignment = CrossAxisAlignment.STRETCH;
        private int gap = 0;
        private SizeConstraint width = SizeConstraint.FILL;
        private SizeConstraint height = SizeConstraint.WRAP;
        private Insets padding = Insets.NONE;
        private int backgroundColor = 0;
        private final List<UIElement> children = new ArrayList<>();

        private Builder(@Nonnull Axis axis) {
            this.axis = axis;
        }

        public Builder crossAxisAlignment(@Nonnull CrossAxisAlignment alignment) {
            this.crossAxisAlignment = alignment;
            return this;
        }

        public Builder gap(int gap) {
            this.gap = gap;
            return this;
        }

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

        public Builder backgroundColor(int color) {
            this.backgroundColor = color;
            return this;
        }

        public Builder child(@Nonnull UIElement child) {
            this.children.add(child);
            return this;
        }

        public Builder children(@Nonnull List<? extends UIElement> children) {
            this.children.addAll(children);
            return this;
        }

        public LinearLayout build() {
            return new LinearLayout(axis, crossAxisAlignment, gap, width, height, padding, backgroundColor, children);
        }

    }

}
