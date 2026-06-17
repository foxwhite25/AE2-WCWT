package com.lhy.wcwt.compat;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.Nullable;

public final class ExtendedAePlusPatternMetadata {
    public static final String ENCODE_PLAYER_TAG = "encodePlayer";

    private ExtendedAePlusPatternMetadata() {
    }

    public static void writeEncoder(ItemStack stack, @Nullable String playerName) {
        if (stack.isEmpty()) {
            return;
        }
        String normalized = normalize(playerName);
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (normalized != null) {
            tag.putString(ENCODE_PLAYER_TAG, normalized);
        } else {
            tag.remove(ENCODE_PLAYER_TAG);
        }
        setCustomDataOrRemove(stack, tag);
    }

    public static ItemStack copyWithoutEncoder(ItemStack stack) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack copy = stack.copy();
        CompoundTag tag = copy.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (tag.contains(ENCODE_PLAYER_TAG)) {
            tag.remove(ENCODE_PLAYER_TAG);
            setCustomDataOrRemove(copy, tag);
        }
        return copy;
    }

    private static void setCustomDataOrRemove(ItemStack stack, CompoundTag tag) {
        if (tag.isEmpty()) {
            stack.remove(DataComponents.CUSTOM_DATA);
        } else {
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
    }

    @Nullable
    private static String normalize(@Nullable String playerName) {
        if (playerName == null) {
            return null;
        }
        String trimmed = playerName.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
