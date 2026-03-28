package dev.breezes.settlements.infrastructure.network.features.ui.stats.codec;

import dev.breezes.settlements.application.ui.stats.model.VillagerInventorySnapshot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

// TODO: Add round-trip codec test once RegistryFriendlyByteBuf test infrastructure is available (mocking)
public final class VillagerInventorySnapshotCodec {

    private static final int MAX_ITEMS = 512;

    public static VillagerInventorySnapshot read(@Nonnull FriendlyByteBuf buffer) {
        int backpackSize = buffer.readVarInt();

        int itemCount = buffer.readVarInt();
        if (itemCount < 0 || itemCount > MAX_ITEMS) {
            throw new IllegalArgumentException("Invalid inventory snapshot itemCount: " + itemCount);
        }

        RegistryFriendlyByteBuf registryBuffer = (RegistryFriendlyByteBuf) buffer;
        List<ItemStack> nonEmptyItems = new ArrayList<>(itemCount);
        for (int i = 0; i < itemCount; i++) {
            nonEmptyItems.add(ItemStack.OPTIONAL_STREAM_CODEC.decode(registryBuffer));
        }

        return VillagerInventorySnapshot.builder()
                .backpackSize(backpackSize)
                .nonEmptyItems(nonEmptyItems)
                .build();
    }

    public static void write(@Nonnull FriendlyByteBuf buffer, @Nonnull VillagerInventorySnapshot snapshot) {
        buffer.writeVarInt(snapshot.backpackSize());

        RegistryFriendlyByteBuf registryBuffer = (RegistryFriendlyByteBuf) buffer;
        buffer.writeVarInt(snapshot.nonEmptyItems().size());
        for (ItemStack stack : snapshot.nonEmptyItems()) {
            ItemStack.OPTIONAL_STREAM_CODEC.encode(registryBuffer, stack);
        }
    }

}
