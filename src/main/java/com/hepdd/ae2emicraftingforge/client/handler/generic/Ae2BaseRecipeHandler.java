package com.hepdd.ae2emicraftingforge.client.handler.generic;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Recipe;

import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantics;
import appeng.menu.me.common.MEStorageMenu;
import appeng.menu.me.items.CraftingTermMenu;
import com.hepdd.ae2emicraftingforge.client.config.Ae2EmiConfig;
import com.hepdd.ae2emicraftingforge.client.helper.InventoryUtils;
import com.hepdd.ae2emicraftingforge.client.helper.rendering.Result;
import dev.emi.emi.api.recipe.EmiPlayerInventory;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.EmiRecipeHandler;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Widget;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class Ae2BaseRecipeHandler<T extends AEBaseMenu> implements EmiRecipeHandler<T> {

    public static final int CRAFTING_GRID_WIDTH = 3;
    public static final int CRAFTING_GRID_HEIGHT = 3;

    private final Class<T> containerClass;
    // Create a thread pool with a single thread for async tasks
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    protected Ae2BaseRecipeHandler(Class<T> containerClass) {
        this.containerClass = containerClass;
    }

    @Override
    public EmiPlayerInventory getInventory(AbstractContainerScreen<T> screen) {
        if (!Ae2EmiConfig.BOMSYNC.get()) {
            return new EmiPlayerInventory(List.of());
        }

        T handler = screen.getMenu();
        if (handler instanceof MEStorageMenu menu) {
            // Asynchronously get the inventory stacks
            Future<EmiPlayerInventory> futureInventory = executorService.submit(() -> {
                List<EmiStack> allStack = new ArrayList<>();

                List<EmiStack> meSystem = InventoryUtils.getExistingStacks(menu);
                allStack.addAll(meSystem);

                List<EmiStack> hotbar = InventoryUtils.getStacks(screen, SlotSemantics.PLAYER_HOTBAR);
                allStack.addAll(hotbar);

                List<EmiStack> inventory = InventoryUtils.getStacks(screen, SlotSemantics.PLAYER_INVENTORY);
                allStack.addAll(inventory);

                if (menu instanceof CraftingTermMenu) {
                    List<EmiStack> craft = InventoryUtils.getStacks(screen, SlotSemantics.CRAFTING_GRID);
                    allStack.addAll(craft);
                }

                return new EmiPlayerInventory(allStack);
            });

            try {
                // Return the computed inventory after the async task completes
                return futureInventory.get();  // Blocks until the async task completes
            } catch (Exception e) {
                e.printStackTrace();
                return EmiPlayerInventory.of(handler.getPlayer());  // Just return player inventory if an error occurs
            }

        } else {
            return EmiPlayerInventory.of(handler.getPlayer());
        }
    }

    protected abstract Result transferRecipe(T menu,
                                             @Nullable Recipe<?> recipe,
                                             EmiRecipe emiRecipe,
                                             boolean doTransfer);

    protected final Result transferRecipe(EmiRecipe emiRecipe, EmiCraftContext<T> context, boolean doTransfer) {
        if (!containerClass.isInstance(context.getScreenHandler())) {
            return Result.createNotApplicable();
        }

        Recipe<?> recipe = context.getScreenHandler()
                .getPlayer()
                .level()
                .getRecipeManager()
                .byKey(emiRecipe.getId())
                .orElse(null);

        T menu = containerClass.cast(context.getScreenHandler());

        var result = transferRecipe(menu, recipe, emiRecipe, doTransfer);
        if (result instanceof Result.Success && doTransfer) {
            Minecraft.getInstance().setScreen(context.getScreen());
        }
        return result;
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        return true;
    }

    @Override
    public boolean canCraft(EmiRecipe recipe, EmiCraftContext<T> context) {
        if (Ae2EmiConfig.ALWAYSSYNC.get() || context.getType() == EmiCraftContext.Type.FILL_BUTTON) {
            return transferRecipe(recipe, context, false).canCraft();
        } else {
            return context.getInventory().canCraft(recipe);
        }
    }

    @Override
    public boolean craft(EmiRecipe recipe, EmiCraftContext<T> context) {
        return transferRecipe(recipe, context, true).canCraft();
    }

    @Override
    public List<ClientTooltipComponent> getTooltip(EmiRecipe recipe, EmiCraftContext<T> context) {
        var tooltip = transferRecipe(recipe, context, false).getTooltip(recipe, context);
        if (tooltip != null) {
            return tooltip.stream()
                    .map(Component::getVisualOrderText)
                    .map(ClientTooltipComponent::create)
                    .toList();
        } else {
            return EmiRecipeHandler.super.getTooltip(recipe, context);
        }
    }

    @Override
    public void render(EmiRecipe recipe, EmiCraftContext<T> context, List<Widget> widgets, GuiGraphics draw) {
        if (context.getType() == EmiCraftContext.Type.FILL_BUTTON) {
            transferRecipe(recipe, context, false).render(recipe, context, widgets, draw);
        } else {
            EmiRecipeHandler.super.render(recipe, context, widgets, draw);
        }
    }

    // Shutdown the executor service when the mod is unloaded (if applicable)
    public static void shutdownExecutorService() {
        executorService.shutdown();
    }
}
