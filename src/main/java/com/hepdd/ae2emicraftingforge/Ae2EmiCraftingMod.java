package com.hepdd.ae2emicraftingforge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import com.hepdd.ae2emicraftingforge.client.config.Ae2EmiConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Ae2EmiCraftingMod.MOD_ID)
public class Ae2EmiCraftingMod {

    public static final String MOD_ID = "ae2emicraftingforge";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public Ae2EmiCraftingMod(FMLJavaModLoadingContext context) {
        context.registerConfig(ModConfig.Type.CLIENT, Ae2EmiConfig.SPEC);

        IEventBus modEventBus = context.getModEventBus();

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register the client setup event handler
        modEventBus.addListener(this::clientSetup);
    }

    @OnlyIn(Dist.CLIENT)
    private void clientSetup(final FMLClientSetupEvent event) {
        LOGGER.info("AE2 EMI Crafting Integration initializing...");

        // EMI integration will be handled through EMI's own plugin system
        // which is automatically discovered by EMI
    }

    /**
     * Checks if a mod is loaded
     *
     * @param modId The mod ID to check
     * @return True if the mod is loaded, false otherwise
     */
    public static boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }
}
