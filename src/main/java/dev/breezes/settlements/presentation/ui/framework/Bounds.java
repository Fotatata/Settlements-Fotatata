package dev.breezes.settlements.presentation.ui.framework;

/**
 * An axis-aligned rectangle in absolute screen pixels.
 * Immutable value produced by the layout pass.
 */
public record Bounds(int x, int y, int width, int height) {

    public static final Bounds ZERO = new Bounds(0, 0, 0, 0);

    public int right() {
        return x + width;
    }

    public int bottom() {
        return y + height;
    }

    public boolean contains(int px, int py) {
        return px >= x && px < right() && py >= y && py < bottom();
    }

}
