package dev.breezes.settlements.presentation.ui.framework;

import dev.breezes.settlements.shared.annotations.functional.ClientSide;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Adapter wrapping a Minecraft {@link AbstractWidget} so it participates in the layout tree.
 * <p>
 * The widget still handles its own rendering, focus, narration, and accessibility.
 * This adapter only synchronizes the widget's position and size during the layout pass.
 * <p>
 * The screen's {@code init()} must still call {@code addRenderableWidget()} for the widget.
 * {@code WidgetElement} just positions it within the framework's layout tree.
 * <p>
 * When a row containing a {@code WidgetElement} is discarded (e.g., by
 * {@link ScrollableList#rebuildRows()}), call {@link #dispose()} to unregister
 * the widget from Minecraft's renderable list.
 */
@ClientSide
public class WidgetElement extends BaseElement {

    @Nullable
    private AbstractWidget widget;

    public WidgetElement(@Nonnull AbstractWidget widget) {
        super(SizeConstraint.fixed(widget.getWidth()), SizeConstraint.fixed(widget.getHeight()), Insets.NONE);
        this.widget = widget;
    }

    @Override
    public void measure(int availableWidth, int availableHeight) {
        if (widget != null) {
            setMeasuredSize(widget.getWidth(), widget.getHeight());
        } else {
            setMeasuredSize(0, 0);
        }
    }

    @Override
    public void layout(@Nonnull Bounds bounds) {
        super.layout(bounds);
        if (widget != null) {
            widget.setPosition(bounds.x(), bounds.y());
        }
    }

    @Override
    public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Rendering is handled by Minecraft's Screen.render() via super.render() which
        // iterates all widgets added with addRenderableWidget(). This is a no-op.
    }

    /**
     * Unregisters this widget element. Call when the containing row is discarded.
     * After disposal, this element measures as 0x0 and renders nothing.
     */
    public void dispose() {
        this.widget = null;
    }

}
