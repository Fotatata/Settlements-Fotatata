package dev.breezes.settlements.presentation.ui.framework;

/**
 * Padding or margin expressed as pixel insets on each side.
 */
public record Insets(int top, int right, int bottom, int left) {

    public static final Insets NONE = new Insets(0, 0, 0, 0);

    public static Insets uniform(int all) {
        return new Insets(all, all, all, all);
    }

    public static Insets symmetric(int vertical, int horizontal) {
        return new Insets(vertical, horizontal, vertical, horizontal);
    }

    public int horizontalTotal() {
        return left + right;
    }

    public int verticalTotal() {
        return top + bottom;
    }

}
