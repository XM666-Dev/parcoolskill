package com.xm666.parcoolskill;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue SKILL_PARTICLE_ENABLED = BUILDER
            .define("skill_particle_enabled", true);

    public static final ModConfigSpec.BooleanValue CLEAVE_ANIMATION_ENABLED = BUILDER
            .define("cleave_animation_enabled", true);

    public static final ModConfigSpec.BooleanValue FLICK_FLACK_ANIMATION_ENABLED = BUILDER
            .define("flick_flack_animation_enabled", true);

    static final ModConfigSpec SPEC = BUILDER.build();
}
