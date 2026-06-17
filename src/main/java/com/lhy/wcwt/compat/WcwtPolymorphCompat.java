package com.lhy.wcwt.compat;

import com.illusivesoulworks.polymorph.api.PolymorphApi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;

import java.util.List;
import java.util.Optional;

public final class WcwtPolymorphCompat {
    private static final String MOD_ID = "polymorph";

    private WcwtPolymorphCompat() {
    }

    public static Optional<RecipeHolder<CraftingRecipe>> getCraftingRecipe(
            AbstractContainerMenu menu, CraftingInput input, Level level, Player player) {
        if (!ModList.get().isLoaded(MOD_ID)) {
            return level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input, level);
        }
        return Impl.getPlayerRecipe(menu, RecipeType.CRAFTING, input, level, player, List.of());
    }

    public static Optional<RecipeHolder<SmithingRecipe>> getSmithingRecipe(
            AbstractContainerMenu menu, SmithingRecipeInput input, Level level, Player player) {
        if (!ModList.get().isLoaded(MOD_ID)) {
            return level.getRecipeManager().getRecipeFor(RecipeType.SMITHING, input, level);
        }
        List<RecipeHolder<SmithingRecipe>> recipes = level.getRecipeManager()
                .getRecipesFor(RecipeType.SMITHING, input, level);
        return Impl.getPlayerRecipe(menu, RecipeType.SMITHING, input, level, player, recipes);
    }

    private static final class Impl {
        private Impl() {
        }

        private static <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeHolder<T>> getPlayerRecipe(
                AbstractContainerMenu menu, RecipeType<T> type, I input, Level level, Player player,
                List<RecipeHolder<T>> recipes) {
            return PolymorphApi.getInstance().getRecipeManager()
                    .getPlayerRecipe(menu, type, input, level, player, recipes);
        }
    }
}
