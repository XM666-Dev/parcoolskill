package com.xm666.parcoolskill.handler;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class SkillHandler {
    public static void addEffect(LivingEntity target, Entity source, Holder<MobEffect> effect, int duration) {
        addEffect(target, source, effect, duration, 0);
    }

    public static void addEffect(LivingEntity target, Entity source, Holder<MobEffect> effect, int duration, int amplifier) {
        var effectInstance = target.getEffect(effect);
        duration += effectInstance != null ? effectInstance.getDuration() : 0;
        target.addEffect(new MobEffectInstance(effect, duration, amplifier), source);
    }
}
