package com.xm666.parcoolskill;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue WILD_STRIKE_ENABLED = BUILDER
            .define("wild_strike_enabled", true);

    public static final ModConfigSpec.BooleanValue SNEAKY_STRIKE_ENABLED = BUILDER
            .define("sneaky_strike_enabled", true);

    public static final ModConfigSpec.BooleanValue DROPKICK_ENABLED = BUILDER
            .define("dropkick_enabled", true);

    public static final ModConfigSpec.BooleanValue HEEL_HOOK_ENABLED = BUILDER
            .define("heel_hook_enabled", true);

    public static final ModConfigSpec.BooleanValue BASH_ENABLED = BUILDER
            .define("bash_enabled", true);

    public static final ModConfigSpec.BooleanValue BACKFLIP_ENABLED = BUILDER
            .define("backflip_enabled", true);

    public static final ModConfigSpec.BooleanValue CLEAVE_ENABLED = BUILDER
            .define("cleave_enabled", true);

    public static final ModConfigSpec.BooleanValue FLICK_FLACK_ENABLED = BUILDER
            .define("flick_flack_enabled", true);

    public static final ModConfigSpec.IntValue WILD_STRIKE_STAMINA_CONSUMPTION = BUILDER
            .defineInRange("wild_strike_stamina_consumption", 50, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue SNEAKY_STRIKE_STAMINA_CONSUMPTION = BUILDER
            .defineInRange("sneaky_strike_stamina_consumption", 400, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue DROPKICK_STAMINA_CONSUMPTION = BUILDER
            .defineInRange("dropkick_stamina_consumption", 50, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue HEEL_HOOK_STAMINA_CONSUMPTION = BUILDER
            .defineInRange("heel_hook_stamina_consumption", 150, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue BASH_STAMINA_CONSUMPTION = BUILDER
            .defineInRange("bash_stamina_consumption", 400, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue BACKFLIP_STAMINA_CONSUMPTION = BUILDER
            .defineInRange("backflip_stamina_consumption", 400, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue CLEAVE_STAMINA_CONSUMPTION = BUILDER
            .defineInRange("cleave_stamina_consumption", 500, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue FLICK_FLACK_STAMINA_CONSUMPTION = BUILDER
            .defineInRange("flick_flack_stamina_consumption", 300, 0, Integer.MAX_VALUE);

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

    public static final ModConfigSpec.DoubleValue SLIDE_SKILL_DAMAGE_MULTIPLIER = BUILDER
            .defineInRange("slide_skill_damage_multiplier", 1.0, 0.0, Double.MAX_VALUE);

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

    public static final ModConfigSpec.IntValue BACKFLIP_SKILL_DURATION = BUILDER
            .defineInRange("backflip_skill_duration", 60, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue BACKFLIP_SKILL_COOLDOWN = BUILDER
            .defineInRange("backflip_skill_cooldown", 60, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue BACKFLIP_BULLET_TIME_SCALE = BUILDER
            .defineInRange("backflip_bullet_time_scale", 0.25, 0.0, Double.MAX_VALUE);

    public static final ModConfigSpec.IntValue BACKFLIP_BULLET_TIME_DURATION = BUILDER
            .defineInRange("backflip_bullet_time_duration", 60, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue CLEAVE_CHARGE_DURATION = BUILDER
            .defineInRange("cleave_charge_duration", 10, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue CLEAVE_ATTACK_DURATION = BUILDER
            .defineInRange("cleave_attack_duration", 10, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue CLEAVE_BULLET_TIME_SCALE = BUILDER
            .defineInRange("cleave_bullet_time_scale", 0.25, 0.0, Double.MAX_VALUE);

    public static final ModConfigSpec.IntValue CLEAVE_BULLET_TIME_DURATION = BUILDER
            .defineInRange("cleave_bullet_time_duration", 20, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue CLEAVE_RANGE_MULTIPLIER_ADDITION = BUILDER
            .defineInRange("cleave_range_multiplier_addition", 1.0, 0.0, Double.MAX_VALUE);

    public static final ModConfigSpec.IntValue CLEAVE_PICK_COUNT_BASE = BUILDER
            .defineInRange("cleave_pick_count_base", 1, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue CLEAVE_PICK_RADIUS = BUILDER
            .defineInRange("cleave_pick_radius", 0.3, 0.0, Double.MAX_VALUE);

    public static final ModConfigSpec.IntValue FLICK_FLACK_NEUTRALIZED_DURATION = BUILDER
            .defineInRange("flick_flack_neutralized_duration", 120, 0, Integer.MAX_VALUE);

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
