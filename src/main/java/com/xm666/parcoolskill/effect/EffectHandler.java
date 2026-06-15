package com.xm666.parcoolskill.effect;

import com.xm666.parcoolskill.Config;
import com.xm666.parcoolskill.ParCoolSkill;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber(modid = ParCoolSkill.MODID)
public class EffectHandler {
    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        var source = event.getSource();
        if (!source.is(Tags.DamageTypes.IS_PHYSICAL) && !source.is(Tags.DamageTypes.IS_MAGIC)) return;

        var damageMultiplier = 1.0F;
        var targetEntity = event.getEntity();
        var sourceEntity = source.getEntity();
        damageMultiplier = applyVulnerable(damageMultiplier, targetEntity, sourceEntity);
        damageMultiplier = applyNeutralized(damageMultiplier, targetEntity, sourceEntity);
        event.setAmount(event.getAmount() * damageMultiplier);
    }

    private static float applyVulnerable(float damageMultiplier, LivingEntity target, Entity source) {
        var vulnerable = target.getEffect(Effects.VULNERABLE);
        if (vulnerable == null) return damageMultiplier;

        var vulnerableDamageMultiplierAddition = Config.VULNERABLE_DAMAGE_MULTIPLIER_ADDITION.get().floatValue();
        var vulnerableDamageMultiplierAdditionGrowth = Config.VULNERABLE_DAMAGE_MULTIPLIER_ADDITION_GROWTH.get().floatValue();
        var amplifier = vulnerable.getAmplifier();
        return damageMultiplier * (1.0F + vulnerableDamageMultiplierAddition + amplifier * vulnerableDamageMultiplierAdditionGrowth);
    }

    private static float applyNeutralized(float damageMultiplier, LivingEntity target, Entity source) {
        if (!(source instanceof LivingEntity living)) return damageMultiplier;

        var neutralized = living.getEffect(Effects.NEUTRALIZED);
        if (neutralized == null) return damageMultiplier;

        var neutralizedDamageMultiplierReduction = Config.NEUTRALIZED_DAMAGE_MULTIPLIER_REDUCTION.get().floatValue();
        var neutralizedDamageMultiplierReductionGrowth = Config.NEUTRALIZED_DAMAGE_MULTIPLIER_REDUCTION_GROWTH.get().floatValue();
        var amplifier = neutralized.getAmplifier();
        return damageMultiplier * (1.0F - neutralizedDamageMultiplierReduction - amplifier * neutralizedDamageMultiplierReductionGrowth);
    }
}
