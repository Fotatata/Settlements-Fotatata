package dev.breezes.settlements.presentation.ui.framework;

import dev.breezes.settlements.shared.annotations.functional.ClientSide;
import net.minecraft.client.gui.GuiGraphics;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * A UI element that can measure itself, be laid out, and render.
 *
 * <p>Lifecycle:</p>
 * <ol>
 *     <li>{@link #measure} — element reports its desired size given available space</li>
 *     <li>{@link #layout} — element is assigned its final pixel position</li>
 *     <li>{@link #render} — called every frame to draw the element</li>
 * </ol>
 */
@ClientSide
public interface UIElement {

    SizeConstraint widthConstraint();

    SizeConstraint heightConstraint();

    void measure(int availableWidth, int availableHeight);

    int measuredWidth();

    int measuredHeight();

    void layout(@Nonnull Bounds bounds);

    Bounds bounds();

    void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTick);

    /**
     * Called after all elements have rendered, for drawing tooltips and other overlays.
     * Tooltips must render last so they appear on top of all other elements.
     */
    default void renderOverlay(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // no-op by default
    }

    /**
     * Called once per game tick for animation state advancement.
     */
    default void tick() {
        // no-op by default
    }

    default boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return false;
    }

    default boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    /**
     * Returns the direct children of this element, if any.
     * Leaf elements return an empty list (default). Containers override to
     * expose their children for generic traversal (e.g., widget disposal).
     */
    default List<UIElement> children() {
        return List.of();
    }

}
