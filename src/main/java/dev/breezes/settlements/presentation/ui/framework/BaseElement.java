package dev.breezes.settlements.presentation.ui.framework;

import dev.breezes.settlements.shared.annotations.functional.ClientSide;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;

/**
 * Abstract base holding the mutable measured-size and bounds fields
 * <p>
 * This is the single point of inheritance in the framework.
 * All containers and leaf elements extend this class.
 */
@ClientSide
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseElement implements UIElement {

    protected final SizeConstraint widthConstraint;
    protected final SizeConstraint heightConstraint;
    protected final Insets padding;

    private int measuredWidth = 0;
    private int measuredHeight = 0;
    private Bounds bounds = Bounds.ZERO;

    @Override
    public SizeConstraint widthConstraint() {
        return widthConstraint;
    }

    @Override
    public SizeConstraint heightConstraint() {
        return heightConstraint;
    }

    @Override
    public int measuredWidth() {
        return measuredWidth;
    }

    @Override
    public int measuredHeight() {
        return measuredHeight;
    }

    @Override
    public Bounds bounds() {
        return bounds;
    }

    protected void setMeasuredSize(int width, int height) {
        this.measuredWidth = width;
        this.measuredHeight = height;
    }

    @Override
    public void layout(@Nonnull Bounds bounds) {
        this.bounds = bounds;
    }

    /**
     * Resolves a {@link SizeConstraint} to a concrete pixel size.
     *
     * @param constraint  the sizing mode
     * @param available   the space offered by the parent
     * @param contentSize the intrinsic content size (used for {@link SizeConstraint.Wrap})
     */
    protected static int resolveSize(@Nonnull SizeConstraint constraint, int available, int contentSize) {
        return switch (constraint) {
            case SizeConstraint.Fixed f -> f.pixels();
            case SizeConstraint.Fill ignored -> available;
            case SizeConstraint.Wrap w -> Math.min(contentSize, Math.min(w.maxPixels(), available));
            case SizeConstraint.Weighted ignored -> available;
            case SizeConstraint.WeightedMax wm -> Math.min(available, wm.maxPixels());
        };
    }

}
