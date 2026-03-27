package dev.breezes.settlements.presentation.ui.framework;

/**
 * Describes how a UI element wants to be sized along one axis.
 * <p>
 * {@link Fill} is syntactic sugar for {@code Weighted(1)}.
 * When multiple {@code Fill} children appear in a {@link LinearLayout},
 * they divide remaining space equally.
 * <p>
 * Constraint propagation rules for pathological nesting:
 *
 * <ul>
 *     <li>{@code Weighted} or {@code Fill} child inside a {@code Wrap} parent:
 *         treated as {@code Fixed(0)} — a {@code Wrap} parent has no "remaining space" to distribute.</li>
 *     <li>{@code Fill} child inside a {@code Fixed} parent:
 *         gets the parent's fixed size minus space used by siblings.</li>
 *     <li>{@code Weighted} children when zero remaining space: each gets {@code Fixed(0)}.</li>
 * </ul>
 */
public sealed interface SizeConstraint {

    /**
     * Use exactly this many pixels.
     */
    record Fixed(int pixels) implements SizeConstraint {
    }

    /**
     * Use all available space. Equivalent to {@code Weighted(1)}.
     */
    record Fill() implements SizeConstraint {
    }

    /**
     * Size to content, up to an optional max.
     */
    record Wrap(int maxPixels) implements SizeConstraint {
        public Wrap() {
            this(Integer.MAX_VALUE);
        }
    }

    /**
     * Proportional share of remaining space (like LinearLayout weight).
     */
    record Weighted(int weight) implements SizeConstraint {
    }

    SizeConstraint FILL = new Fill();
    SizeConstraint WRAP = new Wrap();

    static SizeConstraint fixed(int pixels) {
        return new Fixed(pixels);
    }

    static SizeConstraint weighted(int weight) {
        return new Weighted(weight);
    }

}
