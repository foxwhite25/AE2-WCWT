package com.lhy.wcwt.compat;

import com.illusivesoulworks.polymorph.api.client.PolymorphWidgets;
import com.illusivesoulworks.polymorph.api.client.base.IRecipesWidget;
import com.illusivesoulworks.polymorph.api.client.widgets.children.SelectionWidget;
import com.illusivesoulworks.polymorph.api.client.widgets.PlayerRecipesWidget;
import com.illusivesoulworks.polymorph.api.common.base.IRecipePair;
import com.lhy.wcwt.client.WirelessComprehensiveWorkTerminalScreen;
import com.lhy.wcwt.menu.WcwtSlotSemantics;
import com.lhy.wcwt.menu.WirelessComprehensiveWorkTerminalMenu;
import appeng.menu.SlotSemantics;
import appeng.parts.encoding.EncodingMode;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class WcwtPolymorphClientCompat {
    private static final String MOD_ID = "polymorph";
    private static final int MANUAL_BUTTON_X = 132;
    private static final int MANUAL_BUTTON_Y_OFFSET_FROM_OUTPUT = 19;
    private static boolean registered;

    private WcwtPolymorphClientCompat() {
    }

    public static void registerWidgets() {
        if (registered || !ModList.get().isLoaded(MOD_ID)) {
            return;
        }
        registered = true;
        Impl.registerWidgets();
    }

    private static final class Impl {
        private Impl() {
        }

        private static void registerWidgets() {
            PolymorphWidgets.getInstance().registerWidget(screen -> {
                if (!(screen instanceof WirelessComprehensiveWorkTerminalScreen wcwtScreen)
                        || !(wcwtScreen.getMenu() instanceof WirelessComprehensiveWorkTerminalMenu menu)) {
                    return null;
                }

                List<ModeAwareRecipesWidget> widgets = new ArrayList<>(3);
                Slot previewSlot = firstSlot(menu.getSlots(WcwtSlotSemantics.WCWT_PATTERN_PREVIEW));
                if (previewSlot != null) {
                    widgets.add(new ModeAwareRecipesWidget(wcwtScreen, menu, previewSlot, Placement.PATTERN_PREVIEW));
                }

                Slot manualCraftingResultSlot = firstSlot(menu.getSlots(SlotSemantics.CRAFTING_RESULT));
                if (manualCraftingResultSlot != null) {
                    widgets.add(new ModeAwareRecipesWidget(wcwtScreen, menu, manualCraftingResultSlot,
                            Placement.MANUAL_CRAFTING));
                }

                Slot manualSmithingResultSlot = firstSlot(menu.getSlots(WcwtSlotSemantics.WCWT_MANUAL_SMITHING_RESULT));
                if (manualSmithingResultSlot != null) {
                    widgets.add(new ModeAwareRecipesWidget(wcwtScreen, menu, manualSmithingResultSlot,
                            Placement.MANUAL_SMITHING));
                }
                if (widgets.isEmpty()) {
                    return null;
                }
                return new WcwtRecipesWidget(widgets);
            });
        }

        private static Slot firstSlot(List<Slot> slots) {
            return slots.isEmpty() ? null : slots.getFirst();
        }
    }

    private static final class WcwtRecipesWidget implements IRecipesWidget {
        private final List<ModeAwareRecipesWidget> widgets;

        private WcwtRecipesWidget(List<ModeAwareRecipesWidget> widgets) {
            this.widgets = widgets;
        }

        @Override
        public void initChildWidgets() {
            widgets.forEach(IRecipesWidget::initChildWidgets);
        }

        @Override
        public void selectRecipe(ResourceLocation resourceLocation) {
            primaryWidget().selectRecipe(resourceLocation);
        }

        @Override
        public void highlightRecipe(ResourceLocation resourceLocation) {
            widgets.forEach(widget -> widget.highlightRecipe(resourceLocation));
        }

        @Override
        public void setRecipesList(Set<IRecipePair> recipesList, ResourceLocation selected) {
            Set<IRecipePair> safeRecipes = recipesList == null ? Set.of() : Set.copyOf(recipesList);
            for (ModeAwareRecipesWidget widget : widgets) {
                if (matchesRecipeList(widget, safeRecipes)) {
                    widget.setRecipesList(safeRecipes, selected);
                }
            }
        }

        @Override
        public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float renderPartialTicks) {
            for (ModeAwareRecipesWidget widget : widgets) {
                if (shouldUseWidget(widget)) {
                    widget.render(guiGraphics, mouseX, mouseY, renderPartialTicks);
                }
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            for (ModeAwareRecipesWidget widget : widgets) {
                if (shouldUseWidget(widget) && widget.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public Slot getOutputSlot() {
            return primaryWidget().getOutputSlot();
        }

        @Override
        public SelectionWidget getSelectionWidget() {
            return primaryWidget().getSelectionWidget();
        }

        @Override
        public int getXPos() {
            return primaryWidget().getXPos();
        }

        @Override
        public int getYPos() {
            return primaryWidget().getYPos();
        }

        private IRecipesWidget primaryWidget() {
            for (ModeAwareRecipesWidget widget : widgets) {
                if (widget.isModeActive() && !widget.getOutputSlot().getItem().isEmpty()) {
                    return widget;
                }
            }
            for (ModeAwareRecipesWidget widget : widgets) {
                if (widget.isModeActive()) {
                    return widget;
                }
            }
            for (ModeAwareRecipesWidget widget : widgets) {
                if (shouldUseWidget(widget)) {
                    return widget;
                }
            }
            return widgets.getFirst();
        }

        private boolean shouldUseWidget(ModeAwareRecipesWidget widget) {
            if (!widget.isModeActive()) {
                return false;
            }
            Slot outputSlot = widget.getOutputSlot();
            if (!widget.hasMultipleRecipes() || outputSlot == null || !outputSlot.isActive()) {
                return false;
            }
            ItemStack current = outputSlot.getItem();
            return !current.isEmpty();
        }

        private boolean matchesRecipeList(ModeAwareRecipesWidget widget, Set<IRecipePair> recipesList) {
            if (!widget.isModeActive()) {
                return false;
            }
            Slot outputSlot = widget.getOutputSlot();
            if (outputSlot == null || !outputSlot.isActive()) {
                return false;
            }
            ItemStack current = outputSlot.getItem();
            if (current.isEmpty()) {
                return recipesList.isEmpty();
            }
            for (IRecipePair recipePair : recipesList) {
                if (ItemStack.isSameItemSameComponents(recipePair.getOutput(), current)) {
                    return true;
                }
            }
            return false;
        }
    }

    private enum Placement {
        PATTERN_PREVIEW,
        MANUAL_CRAFTING,
        MANUAL_SMITHING
    }

    private static final class ModeAwareRecipesWidget extends PlayerRecipesWidget {
        private final WirelessComprehensiveWorkTerminalMenu menu;
        private final Placement placement;
        private Set<IRecipePair> recipesList = Set.of();

        private ModeAwareRecipesWidget(WirelessComprehensiveWorkTerminalScreen containerScreen,
                                       WirelessComprehensiveWorkTerminalMenu menu, Slot outputSlot,
                                       Placement placement) {
            super(containerScreen, outputSlot);
            this.menu = menu;
            this.placement = placement;
        }

        @Override
        public int getXPos() {
            return switch (placement) {
                case PATTERN_PREVIEW -> getOutputSlot().x;
                case MANUAL_CRAFTING, MANUAL_SMITHING -> MANUAL_BUTTON_X;
            };
        }

        @Override
        public int getYPos() {
            return switch (placement) {
                case PATTERN_PREVIEW -> getOutputSlot().y + patternPreviewYOffset();
                case MANUAL_CRAFTING, MANUAL_SMITHING ->
                        getOutputSlot().y + MANUAL_BUTTON_Y_OFFSET_FROM_OUTPUT;
            };
        }

        private boolean isModeActive() {
            return switch (placement) {
                case PATTERN_PREVIEW -> menu.getPatternEncodingMode() == EncodingMode.CRAFTING
                        || menu.getPatternEncodingMode() == EncodingMode.SMITHING_TABLE;
                case MANUAL_CRAFTING ->
                        menu.getManualWorkspaceMode() == WirelessComprehensiveWorkTerminalMenu.ManualWorkspaceMode.CRAFTING;
                case MANUAL_SMITHING ->
                        menu.getManualWorkspaceMode() == WirelessComprehensiveWorkTerminalMenu.ManualWorkspaceMode.SMITHING;
            };
        }

        private int patternPreviewYOffset() {
            return menu.getPatternEncodingMode() == EncodingMode.CRAFTING ? -20 : -15;
        }

        private boolean hasMultipleRecipes() {
            return recipesList.size() > 1;
        }

        @Override
        public void setRecipesList(Set<IRecipePair> recipesList, ResourceLocation selected) {
            this.recipesList = recipesList == null ? Set.of() : Set.copyOf(recipesList);
            super.setRecipesList(this.recipesList, selected);
        }

        @Override
        public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float renderPartialTicks) {
            resetWidgetOffsets();
            super.render(guiGraphics, mouseX, mouseY, renderPartialTicks);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            resetWidgetOffsets();
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }
}
