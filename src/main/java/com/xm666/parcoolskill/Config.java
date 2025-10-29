package com.xm666.parcoolskill;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.DoubleValue KICK_BASE_DAMAGE = BUILDER
            .defineInRange("kick_base_damage", 5.0, Double.MIN_VALUE, Double.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue DROPKICK_BASE_KNOCKBACK = BUILDER
            .defineInRange("dropkick_base_knockback", 2.0, Double.MIN_VALUE, Double.MAX_VALUE);

    static final ModConfigSpec SPEC = BUILDER.build();
}
