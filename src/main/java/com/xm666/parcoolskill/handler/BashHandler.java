package com.xm666.parcoolskill.handler;

import com.xm666.parcoolskill.ParCoolSkill;
import com.xm666.parcoolskill.effect.Effects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber(modid = ParCoolSkill.MODID)
public class BashHandler {
    public static boolean queueAttack;

    @SubscribeEvent
    static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        var target = event.getEntity();
        var source = event.getSource();
        if (target.hasEffect(Effects.VULNERABLE) && source.is(Tags.DamageTypes.IS_PHYSICAL)) {
            var targetEffect = target.getEffect(Effects.VULNERABLE);
            event.setAmount(event.getAmount() * (1.5F + 0.5F * (targetEffect != null ? targetEffect.getAmplifier() : 0)));
        }
        if (target instanceof LivingEntity livingTarget && queueAttack) {
            var duration = 120;
            var targetEffect = livingTarget.getEffect(Effects.VULNERABLE);
            duration += targetEffect != null ? targetEffect.getDuration() : 0;
            livingTarget.addEffect(new MobEffectInstance(Effects.VULNERABLE, duration, 0, false, false), source.getEntity());
            queueAttack = false;
        }
    }
}
