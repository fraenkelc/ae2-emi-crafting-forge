package com.hepdd.ae2emicraftingforge.client;

import com.hepdd.ae2emicraftingforge.client.handler.Ae2CraftingHandler;
import com.hepdd.ae2emicraftingforge.client.handler.Ae2PatternTerminalHandler;
import de.mari_023.ae2wtlib.wct.WCTMenu;
import de.mari_023.ae2wtlib.wet.WETMenu;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;

public class Ae2WtEmiPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        // registry.addWorkstation(VanillaEmiRecipeCategories.CRAFTING,
        // EmiStack.of(WUTHandler.wirelessTerminals.get("crafting").universalTerminal()));
        registry.addRecipeHandler(WCTMenu.TYPE, new Ae2CraftingHandler<>(WCTMenu.class));
        registry.addRecipeHandler(WETMenu.TYPE, new Ae2PatternTerminalHandler<>(WETMenu.class));
    }
}
