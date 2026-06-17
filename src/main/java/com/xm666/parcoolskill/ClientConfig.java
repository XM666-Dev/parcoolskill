package com.xm666.parcoolskill;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.ModConfigSpec;

@Mod(value = ParCoolSkill.MODID, dist = Dist.CLIENT)
public class ClientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue SKILL_PARTICLE_ENABLED = BUILDER
            .define("skill_particle_enabled", true);

    public static final ModConfigSpec.BooleanValue CLEAVE_ANIMATION_ENABLED = BUILDER
            .define("cleave_animation_enabled", true);

    public static final ModConfigSpec.BooleanValue FLICK_FLACK_ANIMATION_ENABLED = BUILDER
            .define("flick_flack_animation_enabled", true);

    static final ModConfigSpec SPEC = BUILDER.build();

    public ClientConfig(ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, SPEC);
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}
