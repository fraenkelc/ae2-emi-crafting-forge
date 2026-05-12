package com.hepdd.ae2emicraftingforge.client.mixin;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import appeng.integration.modules.emi.AppEngEmiPlugin;
import appeng.integration.modules.emi.EmiUseCraftingRecipeHandler;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.handler.EmiRecipeHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// AE2's own EMI plugin registers EmiUseCraftingRecipeHandler for these menus,
// conflicting with our ME-aware handlers. Suppress only those two registrations.
@Mixin(value = AppEngEmiPlugin.class, remap = false)
public abstract class AppEngEmiPluginMixin {

    private static final Logger LOGGER = LogManager.getLogger("ae2emicrafting/mixin");

    @Redirect(
              method = "register",
              at = @At(
                       value = "INVOKE",
                       target = "Ldev/emi/emi/api/EmiRegistry;addRecipeHandler(Lnet/minecraft/world/inventory/MenuType;Ldev/emi/emi/api/recipe/handler/EmiRecipeHandler;)V",
                       remap = false))
    private <T extends AbstractContainerMenu> void suppressDefaultCraftingHandlers(
                                                                                   EmiRegistry registry, MenuType<T> type, EmiRecipeHandler<T> handler) {
        if (handler instanceof EmiUseCraftingRecipeHandler) {
            LOGGER.info("[AppEngEmiPluginMixin] suppressed EmiUseCraftingRecipeHandler for {}", type);
            return;
        }
        LOGGER.info("[AppEngEmiPluginMixin] allowing addRecipeHandler for {} with {}", type, handler.getClass().getSimpleName());
        registry.addRecipeHandler(type, handler);
    }
}
