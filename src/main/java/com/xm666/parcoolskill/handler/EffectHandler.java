package com.xm666.parcoolskill.handler;

import com.xm666.parcoolskill.Config;
import com.xm666.parcoolskill.ParCoolSkill;
import com.xm666.parcoolskill.effect.Effects;
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

        var multiplier = 1.0F;
        var sourceEntity = source.getEntity();
        var targetEntity = event.getEntity();

        var vulnerable = targetEntity.getEffect(Effects.VULNERABLE);
        if (vulnerable != null) {
            var vulnerableDamageMultiplierAddition = Config.VULNERABLE_DAMAGE_MULTIPLIER_ADDITION.get().floatValue();
            var vulnerableDamageMultiplierAdditionGrowth = Config.VULNERABLE_DAMAGE_MULTIPLIER_ADDITION_GROWTH.get().floatValue();
            multiplier += vulnerableDamageMultiplierAddition + vulnerable.getAmplifier() * vulnerableDamageMultiplierAdditionGrowth;
        }
        if (sourceEntity instanceof LivingEntity living) {
            var neutralized = living.getEffect(Effects.NEUTRALIZED);
            if (neutralized != null) {
                var neutralizedDamageMultiplierReduction = Config.NEUTRALIZED_DAMAGE_MULTIPLIER_REDUCTION.get().floatValue();
                var neutralizedDamageMultiplierReductionGrowth = Config.NEUTRALIZED_DAMAGE_MULTIPLIER_REDUCTION_GROWTH.get().floatValue();
                multiplier -= neutralizedDamageMultiplierReduction + neutralized.getAmplifier() * neutralizedDamageMultiplierReductionGrowth;
            }
        }

        event.setAmount(event.getAmount() * multiplier);
    }
}
