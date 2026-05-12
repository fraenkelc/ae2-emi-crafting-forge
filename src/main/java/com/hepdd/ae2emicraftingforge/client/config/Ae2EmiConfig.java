package com.hepdd.ae2emicraftingforge.client.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class Ae2EmiConfig {

    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue BOMSYNC;
    public static final ForgeConfigSpec.BooleanValue ALWAYSSYNC;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        BOMSYNC = builder
                .comment(
                        "Sync ME network contents into EMI's item pool.",
                        "Disable on large networks if frame time suffers during recipe lookup.")
                .define("bomsync", true);

        ALWAYSSYNC = builder
                .comment("Always use the ME transfer check for can-craft instead of EMI's own inventory check.")
                .define("alwayssync", false);

        SPEC = builder.build();
    }
}
