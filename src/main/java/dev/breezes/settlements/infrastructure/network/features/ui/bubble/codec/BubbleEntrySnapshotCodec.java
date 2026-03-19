package dev.breezes.settlements.infrastructure.network.features.ui.bubble.codec;

import dev.breezes.settlements.application.ui.bubble.BubbleChannel;
import dev.breezes.settlements.application.ui.bubble.BubbleEntrySnapshot;
import dev.breezes.settlements.application.ui.bubble.BubbleKind;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class BubbleEntrySnapshotCodec {

    private static final int MAX_OWNER_KEY_LENGTH = 128;
    private static final int MAX_TEXT_LENGTH = 256;
    private static final int MAX_EXTRA_DATA_ENTRIES = 32;

    public static BubbleEntrySnapshot read(@Nonnull FriendlyByteBuf buffer) {
        return BubbleEntrySnapshot.builder()
                .bubbleId(buffer.readUUID())
                .channel(buffer.readEnum(BubbleChannel.class))
                .bubbleKind(buffer.readEnum(BubbleKind.class))
                .ownerKey(readOptionalString(buffer, MAX_OWNER_KEY_LENGTH).orElse(null))
                .priority(buffer.readVarInt())
                .expireGameTime(buffer.readLong())
                .createdGameTime(buffer.readLong())
                .sequenceNumber(buffer.readLong())
                .sourceType(buffer.readUtf(MAX_TEXT_LENGTH))
                .extraData(readExtraData(buffer))
                .build();
    }

    public static void write(@Nonnull FriendlyByteBuf buffer, @Nonnull BubbleEntrySnapshot snapshot) {
        buffer.writeUUID(snapshot.bubbleId());
        buffer.writeEnum(snapshot.channel());
        buffer.writeEnum(snapshot.bubbleKind());
        writeOptionalString(buffer, snapshot.ownerKey(), MAX_OWNER_KEY_LENGTH);
        buffer.writeVarInt(snapshot.priority());
        buffer.writeLong(snapshot.expireGameTime());
        buffer.writeLong(snapshot.createdGameTime());
        buffer.writeLong(snapshot.sequenceNumber());
        buffer.writeUtf(snapshot.sourceType(), MAX_TEXT_LENGTH);
        writeExtraData(buffer, snapshot.extraData());
    }

    private static Map<String, String> readExtraData(@Nonnull FriendlyByteBuf buffer) {
        int entryCount = buffer.readVarInt();
        if (entryCount < 0 || entryCount > MAX_EXTRA_DATA_ENTRIES) {
            throw new IllegalArgumentException("Invalid bubble extraData entryCount: " + entryCount);
        }

        Map<String, String> extraData = new LinkedHashMap<>(entryCount);
        for (int i = 0; i < entryCount; i++) {
            String key = buffer.readUtf(MAX_TEXT_LENGTH);
            String value = buffer.readUtf(MAX_TEXT_LENGTH);
            extraData.put(key, value);
        }
        return extraData;
    }

    private static void writeExtraData(@Nonnull FriendlyByteBuf buffer, @Nonnull Map<String, String> extraData) {
        if (extraData.size() > MAX_EXTRA_DATA_ENTRIES) {
            throw new IllegalArgumentException("Too many bubble extraData entries: " + extraData.size());
        }

        buffer.writeVarInt(extraData.size());
        for (Map.Entry<String, String> entry : extraData.entrySet()) {
            buffer.writeUtf(entry.getKey(), MAX_TEXT_LENGTH);
            buffer.writeUtf(entry.getValue(), MAX_TEXT_LENGTH);
        }
    }

    private static Optional<String> readOptionalString(@Nonnull FriendlyByteBuf buffer, int maxLength) {
        return buffer.readBoolean() ? Optional.of(buffer.readUtf(maxLength)) : Optional.empty();
    }

    private static void writeOptionalString(@Nonnull FriendlyByteBuf buffer, @Nullable String value, int maxLength) {
        boolean present = value != null;
        buffer.writeBoolean(present);
        if (present) {
            buffer.writeUtf(value, maxLength);
        }
    }

}
