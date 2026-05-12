package com.hepdd.ae2emicraftingforge.client;

import appeng.menu.me.common.MEStorageMenu;
import appeng.menu.me.items.CraftingTermMenu;
import appeng.menu.me.items.PatternEncodingTermMenu;
import com.hepdd.ae2emicraftingforge.client.handler.Ae2CraftingHandler;
import com.hepdd.ae2emicraftingforge.client.handler.Ae2MeTerminalHandler;
import com.hepdd.ae2emicraftingforge.client.handler.Ae2PatternTerminalHandler;
import com.hepdd.ae2emicraftingforge.client.helper.mapper.EmiFluidStackConverter;
import com.hepdd.ae2emicraftingforge.client.helper.mapper.EmiItemStackConverter;
import com.hepdd.ae2emicraftingforge.client.helper.mapper.EmiStackConverters;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;

public class Ae2EmiPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        EmiStackConverters.register(new EmiItemStackConverter());
        EmiStackConverters.register(new EmiFluidStackConverter());

        registry.addRecipeHandler(CraftingTermMenu.TYPE, new Ae2CraftingHandler<>(CraftingTermMenu.class));
        registry.addRecipeHandler(PatternEncodingTermMenu.TYPE, new Ae2PatternTerminalHandler<>(PatternEncodingTermMenu.class));
        // Workaround: Seeing Items from ME Terminal on Synthetic Favourites without using the GenericStackProvider.
        // Reasoning: For whatever reason that is broken on fluids, even though it shouldn't.
        registry.addRecipeHandler(MEStorageMenu.TYPE, new Ae2MeTerminalHandler<>(MEStorageMenu.class));
    }
}
