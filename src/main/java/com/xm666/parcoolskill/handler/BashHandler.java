package com.xm666.parcoolskill.handler;

import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import com.alrex.parcool.common.action.impl.ChargeJump;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.xm666.parcoolskill.ParCoolSkill;
import com.xm666.parcoolskill.action.BashJump;
import com.xm666.parcoolskill.effect.Effects;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber(modid = ParCoolSkill.MODID)
public class BashHandler {
    @SubscribeEvent
    static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        var entity = event.getEntity();
        var source = event.getSource();
        if (entity.hasEffect(Effects.VULNERABLE) && source.is(Tags.DamageTypes.IS_PHYSICAL)) {
            var targetEffect = entity.getEffect(Effects.VULNERABLE);
            event.setAmount(event.getAmount() * (1.5F + 0.5F * (targetEffect != null ? targetEffect.getAmplifier() : 0)));
        }
        if (source.getEntity() instanceof Player player && source.is(DamageTypes.PLAYER_ATTACK)) {
            var jump = (BashJump) Parkourability.get(player).get(ChargeJump.class);
            if (!jump.parcoolskill$isQueueAttack()) return;
            var duration = 120;
            var targetEffect = entity.getEffect(Effects.VULNERABLE);
            duration += targetEffect != null ? targetEffect.getDuration() : 0;
            entity.addEffect(new MobEffectInstance(Effects.VULNERABLE, duration, 0, false, false), player);
            jump.parcoolskill$setQueueAttack(false);
        }
    }

    @SubscribeEvent
    public static void onJumpStart(ParCoolActionEvent.StartEvent event) {
        if (!(event.getAction() instanceof BashJump jump)) return;
        jump.parcoolskill$setQueueAttack(true);
    }

    @SubscribeEvent
    public static void onJumpStop(ParCoolActionEvent.StopEvent event) {
        if (!(event.getAction() instanceof BashJump jump)) return;
        jump.parcoolskill$setQueueAttack(false);
    }
}
