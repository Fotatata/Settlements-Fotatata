package dev.breezes.settlements.presentation.ui.framework;

import dev.breezes.settlements.shared.annotations.functional.ClientSide;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Abstract base screen that bridges the layout framework into Minecraft's {@link Screen} system.
 *
 * <p>Subclasses implement {@link #buildRoot()} to declaratively define their UI tree.
 * The framework handles measure → layout → render automatically.</p>
 *
 * <p>Keyboard focus is delegated to Minecraft's widget system via {@link WidgetElement} —
 * framework-only elements are not focusable in v1.</p>
 */
@ClientSide
public abstract class LayoutScreen extends Screen {

    @Getter
    protected final UITheme theme;

    @Nullable
    private UIElement root;

    @Nullable
    private UIElement popup;
    private int popupX;
    private int popupY;

    protected LayoutScreen(@Nonnull Component title, @Nonnull UITheme theme) {
        super(title);
        this.theme = theme;
    }

    protected LayoutScreen(@Nonnull Component title) {
        this(title, UITheme.DEFAULT);
    }

    /**
     * Subclasses build their element tree here.
     * Called during {@link #init()} and on resize.
     */
    protected abstract UIElement buildRoot();

    @Override
    protected void init() {
        this.root = buildRoot();
        performLayout();
    }

    @Override
    public void renderBackground(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, this.width, this.height, this.theme.backgroundDimColor());
    }

    @Override
    public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);

        if (this.root != null) {
            this.root.render(graphics, mouseX, mouseY, partialTick);
        }

        // Render Minecraft widgets (buttons, etc.)
        super.render(graphics, mouseX, mouseY, partialTick);

        // Render overlays (tooltips) after everything else
        if (this.root != null) {
            this.root.renderOverlay(graphics, mouseX, mouseY, partialTick);
        }

        // Render popup last (always on top)
        if (this.popup != null) {
            this.popup.render(graphics, mouseX, mouseY, partialTick);
            this.popup.renderOverlay(graphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.root != null) {
            this.root.tick();
        }
        if (this.popup != null) {
            this.popup.tick();
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.popup != null && this.popup.bounds().contains((int) mouseX, (int) mouseY)) {
            return popup.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        }

        if (this.root != null && this.root.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Popup is modal — clicks outside dismiss it
        if (this.popup != null) {
            if (this.popup.bounds().contains((int) mouseX, (int) mouseY)) {
                return this.popup.mouseClicked(mouseX, mouseY, button);
            } else {
                dismissPopup();
                return true;
            }
        }

        if (this.root != null && this.root.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    /**
     * Force re-layout. Call after data changes that affect sizes.
     */
    protected void invalidateLayout() {
        if (this.root != null) {
            performLayout();
        }
    }

    /**
     * Show a modal popup at the given position.
     * Clicks outside the popup dismiss it. A second call replaces the current popup.
     * The popup is clamped to screen bounds.
     */
    protected void showPopup(@Nonnull UIElement popup, int x, int y) {
        this.popup = popup;

        popup.measure(this.width, this.height);
        int clampedX = Mth.clamp(x, 0, Math.max(0, this.width - popup.measuredWidth()));
        int clampedY = Mth.clamp(y, 0, Math.max(0, this.height - popup.measuredHeight()));
        this.popupX = clampedX;
        this.popupY = clampedY;

        popup.layout(new Bounds(clampedX, clampedY, popup.measuredWidth(), popup.measuredHeight()));
    }

    protected void dismissPopup() {
        this.popup = null;
    }

    private void performLayout() {
        if (this.root == null) {
            return;
        }
        this.root.measure(this.width, this.height);

        // Center the root element if it's smaller than the screen
        int rootX = (this.width - this.root.measuredWidth()) / 2;
        int rootY = (this.height - this.root.measuredHeight()) / 2;
        this.root.layout(new Bounds(rootX, rootY, this.root.measuredWidth(), this.root.measuredHeight()));
    }

}
