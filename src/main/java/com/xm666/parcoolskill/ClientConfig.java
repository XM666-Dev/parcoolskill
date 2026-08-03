package com.xm666.parcoolskill;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.config.ModConfig;

public class ClientConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.BooleanValue SKILL_PARTICLE_ENABLED = BUILDER
            .define("skill_particle_enabled", true);

    public static final ForgeConfigSpec.BooleanValue CLEAVE_ANIMATION_ENABLED = BUILDER
            .define("cleave_animation_enabled", true);

    public static final ForgeConfigSpec.BooleanValue FLICK_FLACK_ANIMATION_ENABLED = BUILDER
            .define("flick_flack_animation_enabled", true);

    private static final ForgeConfigSpec SPEC = BUILDER.build();

    public static void init(ModContainer container) {
        Config.registerConfig(ModConfig.Type.CLIENT, SPEC, container);
    }
}
