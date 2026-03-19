package dev.breezes.settlements.infrastructure.network.features.ui.bubble.codec;

import dev.breezes.settlements.application.ui.bubble.BubbleChannel;
import dev.breezes.settlements.application.ui.bubble.BubbleEntrySnapshot;
import dev.breezes.settlements.application.ui.bubble.BubbleKind;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

class BubbleEntrySnapshotCodecTest {

    @Test
    void bubbleEntrySnapshot_roundtrip_preservesFields() {
        BubbleEntrySnapshot input = BubbleEntrySnapshot.builder()
                .bubbleId(UUID.randomUUID())
                .channel(BubbleChannel.BEHAVIOR)
                .bubbleKind(BubbleKind.TEST_ONLY)
                .ownerKey("test:codec_roundtrip")
                .priority(50)
                .expireGameTime(400)
                .createdGameTime(200)
                .sequenceNumber(3)
                .sourceType("behavior")
                .extraData(Map.of("variant", "default"))
                .build();

        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        BubbleEntrySnapshotCodec.write(buffer, input);

        BubbleEntrySnapshot decoded = BubbleEntrySnapshotCodec.read(buffer);
        Assertions.assertEquals(input, decoded);
    }

    @Test
    void bubbleEntrySnapshot_roundtrip_preservesNullOwnerKey() {
        BubbleEntrySnapshot input = BubbleEntrySnapshot.builder()
                .bubbleId(UUID.randomUUID())
                .channel(BubbleChannel.CHAT)
                .bubbleKind(BubbleKind.TEST_ONLY)
                .ownerKey(null)
                .priority(5)
                .expireGameTime(90)
                .createdGameTime(10)
                .sequenceNumber(1)
                .sourceType("sensor")
                .extraData(Map.of())
                .build();

        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        BubbleEntrySnapshotCodec.write(buffer, input);

        BubbleEntrySnapshot decoded = BubbleEntrySnapshotCodec.read(buffer);
        Assertions.assertEquals(input, decoded);
    }

}