package dev.breezes.settlements.presentation.ui.stats;

import dev.breezes.settlements.presentation.ui.framework.BaseElement;
import dev.breezes.settlements.presentation.ui.framework.Bounds;
import dev.breezes.settlements.presentation.ui.framework.Insets;
import dev.breezes.settlements.presentation.ui.framework.SizeConstraint;
import dev.breezes.settlements.presentation.ui.framework.UITheme;
import dev.breezes.settlements.shared.annotations.functional.ClientSide;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import javax.annotation.Nonnull;

/**
 * A UI element that renders a health bar with label and fraction text.
 * <p>
 * Layout: {@code "Health: [===bar===] 18/20"}
 * <p>
 * The HP label and fraction text are cached and only recalculated when
 * {@link #setHp(float, float)} is called (on snapshot update), avoiding
 * per-frame font width measurement.
 */
@ClientSide
class HpBarElement extends BaseElement {

    private static final String HEALTH_KEY = "ui.settlements.stats.health";
    private static final int BAR_BACKGROUND_COLOR = 0xFF333333;
    private static final float HP_MID_THRESHOLD = 0.50F;
    private static final float HP_LOW_THRESHOLD = 0.25F;
    private static final int LABEL_BAR_GAP = 3;
    private static final int BAR_Y_OFFSET = 1;
    private static final int BAR_HEIGHT = 7;

    private final UITheme theme;
    private final Component healthLabel;
    private final int healthLabelWidth;

    // Cached render state
    private float currentHp;
    private float maxHp;
    private String hpFractionText;
    private int hpFractionWidth;

    HpBarElement(@Nonnull UITheme theme, float initialHp, float initialMaxHp) {
        super(SizeConstraint.FILL, SizeConstraint.fixed(9), Insets.NONE);

        this.theme = theme;
        this.healthLabel = Component.translatable(HEALTH_KEY);

        Font font = Minecraft.getInstance().font;
        this.healthLabelWidth = font.width(this.healthLabel);

        updateHpCache(initialHp, initialMaxHp, font);
    }

    void setHp(float hp, float maxHp) {
        if (this.currentHp == hp && this.maxHp == maxHp) {
            return;
        }

        Font font = Minecraft.getInstance().font;
        updateHpCache(hp, maxHp, font);
    }

    private void updateHpCache(float hp, float maxHp, @Nonnull Font font) {
        this.currentHp = hp;
        this.maxHp = maxHp;

        this.hpFractionText = Math.round(hp) + "/" + Math.round(maxHp);
        this.hpFractionWidth = font.width(this.hpFractionText);
    }

    @Override
    public void measure(int availableWidth, int availableHeight) {
        int w = resolveSize(widthConstraint, availableWidth, availableWidth);
        int h = resolveSize(heightConstraint, availableHeight, 9);
        setMeasuredSize(w, h);
    }

    @Override
    public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        Font font = Minecraft.getInstance().font;
        Bounds b = bounds();
        float ratio = maxHp > 0 ? Mth.clamp(currentHp / maxHp, 0, 1) : 0;

        // Health label on the left
        graphics.drawString(font, healthLabel, b.x(), b.y(), theme.subtleTextColor(), false);

        // Fraction display on the right
        int hpTextX = b.right() - hpFractionWidth;
        graphics.drawString(font, hpFractionText, hpTextX, b.y(), theme.textColor(), false);

        // Bar fills the space between label and fraction
        int barX = b.x() + healthLabelWidth + LABEL_BAR_GAP;
        int barEndX = hpTextX - LABEL_BAR_GAP;
        int barWidth = barEndX - barX;
        if (barWidth > 2) {
            int barY = b.y() + BAR_Y_OFFSET;
            graphics.fill(barX, barY, barEndX, barY + BAR_HEIGHT, BAR_BACKGROUND_COLOR);
            int filledWidth = Math.round(barWidth * ratio);
            if (filledWidth > 0) {
                int barColor = ratio > HP_MID_THRESHOLD ? theme.successColor()
                        : ratio > HP_LOW_THRESHOLD ? theme.warningColor()
                        : theme.errorColor();
                graphics.fill(barX, barY, barX + filledWidth, barY + BAR_HEIGHT, barColor);
            }
        }
    }

}
