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

    public static final ModConfigSpec.DoubleValue SLIDE_SKILL_DAMAGE_ADDITION = BUILDER
            .defineInRange("slide_skill_damage_addition", 4.0, 0.0, Double.MAX_VALUE);

    public static final ModConfigSpec.IntValue SLIDE_SKILL_EXTRA_INVULNERABLE_DURATION = BUILDER
            .defineInRange("slide_skill_extra_invulnerable_duration", 10, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue SLIDE_SKILL_BULLET_TIME_SCALE = BUILDER
            .defineInRange("slide_skill_bullet_time_scale", 0.25, 0.0, Double.MAX_VALUE);

    public static final ModConfigSpec.IntValue SLIDE_SKILL_BULLET_TIME_DURATION = BUILDER
            .defineInRange("slide_skill_bullet_time_duration", 80, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue DROPKICK_KNOCKBACK_BASE = BUILDER
            .defineInRange("dropkick_knockback_base", 2.0, 0.0, Double.MAX_VALUE);

    public static final ModConfigSpec.IntValue HEEL_HOOK_SLOWDOWN_DURATION = BUILDER
            .defineInRange("heel_hook_slowdown_duration", 60, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue HEEL_HOOK_SLOWDOWN_AMPLIFIER = BUILDER
            .defineInRange("heel_hook_slowdown_amplifier", 2, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue BASH_VULNERABLE_DURATION = BUILDER
            .defineInRange("bash_vulnerable_duration", 120, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue CLEAVE_CHARGE_DURATION = BUILDER
            .defineInRange("cleave_charge_duration", 10, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue CLEAVE_STAMINA_CONSUMPTION = BUILDER
            .defineInRange("cleave_stamina_consumption", 400, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue CLEAVE_ATTACK_DURATION = BUILDER
            .defineInRange("cleave_attack_duration", 10, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue CLEAVE_BULLET_TIME_SCALE = BUILDER
            .defineInRange("cleave_bullet_time_scale", 0.25, 0, Double.MAX_VALUE);

    public static final ModConfigSpec.IntValue CLEAVE_BULLET_TIME_DURATION = BUILDER
            .defineInRange("cleave_bullet_time_duration", 20, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue CLEAVE_HIT_RANGE_MULTIPLIER = BUILDER
            .defineInRange("cleave_hit_range_multiplier", 2.0, 0, Double.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue CLEAVE_HIT_RADIUS = BUILDER
            .defineInRange("cleave_hit_radius", 0.3, 0, Double.MAX_VALUE);

    public static final ModConfigSpec.IntValue CLEAVE_HIT_LIMIT_BASE = BUILDER
            .defineInRange("cleave_hit_limit_base", 1, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue BACKFLIP_SKILL_DURATION = BUILDER
            .defineInRange("backflip_skill_duration", 60, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue BACKFLIP_SKILL_COOLDOWN = BUILDER
            .defineInRange("backflip_skill_cooldown", 60, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue BACKFLIP_BULLET_TIME_SCALE = BUILDER
            .defineInRange("backflip_bullet_time_scale", 0.25, 0.0, Double.MAX_VALUE);

    public static final ModConfigSpec.IntValue BACKFLIP_BULLET_TIME_DURATION = BUILDER
            .defineInRange("backflip_bullet_time_duration", 60, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue LEG_SWEEP_NEUTRALIZED_DURATION = BUILDER
            .defineInRange("leg_sweep_neutralized_duration", 120, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue LEG_SWEEP_KNOCKBACK_BASE = BUILDER
            .defineInRange("leg_sweep_knockback_base", 1.0, 0.0, Double.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue VULNERABLE_DAMAGE_MULTIPLIER_ADDITION = BUILDER
            .defineInRange("vulnerable_damage_multiplier_addition", 0.5, 0.0, Double.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue VULNERABLE_DAMAGE_MULTIPLIER_ADDITION_GROWTH = BUILDER
            .defineInRange("vulnerable_damage_multiplier_addition_growth", 0.25, 0.0, Double.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue NEUTRALIZED_DAMAGE_MULTIPLIER_REDUCTION = BUILDER
            .defineInRange("neutralized_damage_multiplier_reduction", 0.25, 0.0, Double.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue NEUTRALIZED_DAMAGE_MULTIPLIER_REDUCTION_GROWTH = BUILDER
            .defineInRange("neutralized_damage_multiplier_reduction_growth", 0.15, 0.0, Double.MAX_VALUE);

    static final ModConfigSpec SPEC = BUILDER.build();
}
