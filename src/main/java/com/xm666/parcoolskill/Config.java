package com.xm666.parcoolskill;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.DoubleValue WILD_STRIKE_DAMAGE_MULTIPLIER = BUILDER
            .defineInRange("wild_strike_damage_multiplier", 2.0, 0.0, Double.MAX_VALUE);

    public static final ModConfigSpec.IntValue WILD_STRIKE_PARRY_DURATION = BUILDER
            .defineInRange("wild_strike_parry_duration", 10, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue SNEAKY_STRIKE_READY_DURATION = BUILDER
            .defineInRange("sneaky_strike_ready_duration", 20, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue SNEAKY_STRIKE_DAMAGE_MULTIPLIER = BUILDER
            .defineInRange("sneaky_strike_damage_multiplier", 2.0, 0.0, Double.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue BACKSTAB_DAMAGE_MULTIPLIER = BUILDER
            .defineInRange("backstab_damage_multiplier", 3.0, 0.0, Double.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue SLIDE_SKILL_BASE_DAMAGE = BUILDER
            .defineInRange("slide_skill_base_damage", 4.0, 0.0, Double.MAX_VALUE);

    public static final ModConfigSpec.IntValue SLIDE_SKILL_INVULNERABLE_DURATION = BUILDER
            .defineInRange("slide_skill_invulnerable_duration", 10, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue SLIDE_SKILL_BULLET_TIME_SCALE = BUILDER
            .defineInRange("slide_skill_bullet_time_scale", 0.25, 0.0, Double.MAX_VALUE);

    public static final ModConfigSpec.IntValue SLIDE_SKILL_BULLET_TIME_DURATION = BUILDER
            .defineInRange("slide_skill_bullet_time_duration", 80, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue DROPKICK_BASE_KNOCKBACK = BUILDER
            .defineInRange("dropkick_base_knockback", 2.0, 0.0, Double.MAX_VALUE);

    public static final ModConfigSpec.IntValue HEEL_HOOK_SLOWDOWN_DURATION = BUILDER
            .defineInRange("heel_hook_slowdown_duration", 60, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue HEEL_HOOK_SLOWDOWN_AMPLIFIER = BUILDER
            .defineInRange("heel_hook_slowdown_amplifier", 2, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue BASH_VULNERABLE_DURATION = BUILDER
            .defineInRange("bash_vulnerable_duration", 120, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue CLEAVE_CHARGE_DURATION = BUILDER
            .defineInRange("cleave_charge_duration", 10, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue CLEAVE_COOLDOWN_DURATION = BUILDER
            .defineInRange("cleave_cooldown_duration", 10, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue CLEAVE_STAMINA_CONSUMPTION = BUILDER
            .defineInRange("cleave_stamina_consumption", 400, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue CLEAVE_ATTACK_DURATION = BUILDER
            .defineInRange("cleave_attack_duration", 10, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue CLEAVE_BULLET_TIME_SCALE = BUILDER
            .defineInRange("cleave_bullet_time_scale", 0.25, 0, Double.MAX_VALUE);

    public static final ModConfigSpec.IntValue CLEAVE_BULLET_TIME_DURATION = BUILDER
            .defineInRange("cleave_bullet_time_duration", 20, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue CLEAVE_INTERACTION_MULTIPLIER = BUILDER
            .defineInRange("cleave_interaction_multiplier", 2.0, 0, Double.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue CLEAVE_INTERACTION_RADIUS = BUILDER
            .defineInRange("cleave_interaction_radius", 0.3, 0, Double.MAX_VALUE);

    public static final ModConfigSpec.IntValue CLEAVE_BASE_HIT_LIMIT = BUILDER
            .defineInRange("cleave_base_hit_limit", 1, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue CLEAVE_HIT_LIMIT_INCREASE = BUILDER
            .defineInRange("cleave_hit_limit_increase", 1, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue VULNERABLE_BASE_DAMAGE_MULTIPLIER = BUILDER
            .defineInRange("vulnerable_base_damage_multiplier", 1.5, 0.0, Double.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue VULNERABLE_DAMAGE_MULTIPLIER_INCREASE = BUILDER
            .defineInRange("vulnerable_damage_multiplier_increase", 0.25, 0.0, Double.MAX_VALUE);

    static final ModConfigSpec SPEC = BUILDER.build();
}
