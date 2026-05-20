package com.lhy.wcwt.pull;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.menu.me.common.GridInventoryEntry;
import appeng.menu.me.common.MEStorageMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class WcwtIngredientPriorities {
    private static final Comparator<GridInventoryEntry> ENTRY_COMPARATOR = Comparator
            .comparing(GridInventoryEntry::isCraftable)
            .thenComparing(WcwtIngredientPriorities::isUndamaged)
            .thenComparing(GridInventoryEntry::getStoredAmount);

    private WcwtIngredientPriorities() {
    }

    public static Map<AEKey, Integer> getIngredientPriorities(@Nullable MEStorageMenu menu) {
        if (menu == null || menu.getClientRepo() == null) {
            return Map.of();
        }

        var orderedEntries = menu.getClientRepo().getAllEntries().stream()
                .sorted(ENTRY_COMPARATOR)
                .map(GridInventoryEntry::getWhat)
                .toList();

        var result = new HashMap<AEKey, Integer>(orderedEntries.size());
        for (int i = 0; i < orderedEntries.size(); i++) {
            var key = orderedEntries.get(i);
            if (key != null) {
                result.put(key, i);
            }
        }

        for (var item : menu.getPlayerInventory().items) {
            var key = AEItemKey.of(item);
            if (key != null) {
                result.putIfAbsent(key, -1);
            }
        }

        return result;
    }

    public static List<ItemStack> deduplicateItemAlternatives(List<ItemStack> alternatives) {
        if (alternatives == null || alternatives.isEmpty()) {
            return List.of();
        }

        List<ItemStack> deduplicated = new ArrayList<>(alternatives.size());
        for (var alternative : alternatives) {
            if (alternative == null || alternative.isEmpty() || containsEquivalentStack(deduplicated, alternative)) {
                continue;
            }
            deduplicated.add(alternative.copy());
        }
        return deduplicated;
    }

    public static List<ItemStack> sortItemAlternatives(@Nullable MEStorageMenu menu, List<ItemStack> alternatives) {
        List<ItemStack> sorted = new ArrayList<>(deduplicateItemAlternatives(alternatives));
        if (sorted.size() <= 1) {
            return sorted;
        }

        var priorities = getIngredientPriorities(menu);
        sorted.sort(Comparator
                .comparingInt((ItemStack stack) -> getPriority(priorities, stack)).reversed()
                .thenComparing(Comparator.comparingInt(WcwtPullIngredientOrdering::componentSpecificityRank).reversed())
                .thenComparingLong(ItemStack::hashItemAndComponents));
        return sorted;
    }

    @Nullable
    public static GenericStack chooseBestGenericStack(@Nullable MEStorageMenu menu, List<GenericStack> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return null;
        }
        if (candidates.size() == 1) {
            return candidates.getFirst();
        }

        var priorities = getIngredientPriorities(menu);
        return candidates.stream()
                .max(Comparator
                        .comparingInt((GenericStack stack) -> getPriority(priorities, stack.what()))
                        .thenComparingInt(WcwtPullIngredientOrdering::genericStackItemSpecificityRank))
                .orElse(null);
    }

    public static ItemStack chooseBestItem(@Nullable MEStorageMenu menu,
                                           Ingredient ingredient,
                                           List<ItemStack> visibleAlternatives) {
        ItemStack bestNetworkIngredient = findBestNetworkIngredient(menu, ingredient);
        if (!bestNetworkIngredient.isEmpty()) {
            return bestNetworkIngredient;
        }

        for (var visibleAlternative : sortItemAlternatives(menu, visibleAlternatives)) {
            if (ingredient.test(visibleAlternative)) {
                return visibleAlternative.copy();
            }
        }

        ItemStack[] items = ingredient.getItems();
        return items.length > 0 ? items[0].copy() : ItemStack.EMPTY;
    }

    private static ItemStack findBestNetworkIngredient(@Nullable MEStorageMenu menu, Ingredient ingredient) {
        var priorities = getIngredientPriorities(menu);
        return priorities.entrySet().stream()
                .filter(entry -> entry.getKey() instanceof AEItemKey itemKey && itemKey.matches(ingredient))
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .filter(AEItemKey.class::isInstance)
                .map(AEItemKey.class::cast)
                .map(AEItemKey::toStack)
                .orElse(ItemStack.EMPTY);
    }

    private static boolean isUndamaged(GridInventoryEntry entry) {
        return !(entry.getWhat() instanceof AEItemKey itemKey) || !itemKey.isDamaged();
    }

    private static int getPriority(Map<AEKey, Integer> priorities, ItemStack stack) {
        return getPriority(priorities, AEItemKey.of(stack));
    }

    private static int getPriority(Map<AEKey, Integer> priorities, @Nullable AEKey key) {
        return key == null ? Integer.MIN_VALUE : priorities.getOrDefault(key, Integer.MIN_VALUE);
    }

    private static boolean containsEquivalentStack(List<ItemStack> stacks, ItemStack candidate) {
        for (var existing : stacks) {
            if (ItemStack.isSameItemSameComponents(existing, candidate)) {
                return true;
            }
        }
        return false;
    }
}
