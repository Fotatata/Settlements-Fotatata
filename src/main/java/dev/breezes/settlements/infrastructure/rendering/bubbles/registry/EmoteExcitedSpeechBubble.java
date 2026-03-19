package dev.breezes.settlements.infrastructure.rendering.bubbles.registry;

import dev.breezes.settlements.domain.time.Ticks;
import dev.breezes.settlements.infrastructure.rendering.bubbles.canvas.BubbleBoundaryElement;
import dev.breezes.settlements.infrastructure.rendering.bubbles.canvas.BubbleTextElement;
import dev.breezes.settlements.infrastructure.rendering.bubbles.canvas.DefaultSpeechBubble;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;

import javax.annotation.Nonnull;
import java.util.UUID;

public class EmoteExcitedSpeechBubble extends DefaultSpeechBubble {

    private static BubbleBoundaryElement createBoundaryElement() {
        BubbleTextElement textElement = BubbleTextElement.builder()
                .font(Minecraft.getInstance().font)
                .color(ChatFormatting.GOLD)
                .message("!")
                .maxWidth(40)
                .build();

        return BubbleBoundaryElement.builder()
                .innerElement(textElement)
                .opacity(1.0F)
                .build();
    }

    public EmoteExcitedSpeechBubble(@Nonnull UUID uuid,
                                    double visibilityBlocks,
                                    @Nonnull Ticks lifetime) {
        super(uuid, createBoundaryElement(), visibilityBlocks, lifetime);
    }

}