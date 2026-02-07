package com.xm666.parcoolskill.handler;

import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import com.alrex.parcool.common.action.impl.ChargeJump;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.xm666.parcoolskill.ParCoolSkill;
import com.xm666.parcoolskill.effect.Effects;
import com.xm666.parcoolskill.event.PlayerAttackEvent;
import com.xm666.parcoolskill.skill.JumpSkill;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber(modid = ParCoolSkill.MODID)
public class BashHandler {
    @SubscribeEvent
    static void onJumpStart(ParCoolActionEvent.Start.Pre event) {
        if (!(event.getAction() instanceof JumpSkill jumpSkill)) return;

        jumpSkill.parcoolskill$setAttackReady(true);
    }

    @SubscribeEvent
    static void onJumpFinish(ParCoolActionEvent.Finish.Pre event) {
        if (!(event.getAction() instanceof JumpSkill jumpSkill)) return;

        jumpSkill.parcoolskill$setAttackReady(false);
    }

    @SubscribeEvent
    static void onPlayerAttack(PlayerAttackEvent.Post event) {
        var player = event.getEntity();

        var skillJump = (JumpSkill) Parkourability.get(player).get(ChargeJump.class);
        if (!skillJump.parcoolskill$isAttackReady()) return;
        skillJump.parcoolskill$setAttackReady(false);

        if (!event.getCritEvent().isVanillaCritical()) return;

        var target = event.getTarget();
        var duration = 120;
        var targetEffect = target.getEffect(Effects.VULNERABLE);
        duration += targetEffect != null ? targetEffect.getDuration() : 0;
        target.addEffect(new MobEffectInstance(Effects.VULNERABLE, duration), player);
    }

    @SubscribeEvent
    static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        var target = event.getEntity();
        var targetEffect = target.getEffect(Effects.VULNERABLE);
        if (targetEffect == null) return;

        var source = event.getSource();
        if (!source.is(Tags.DamageTypes.IS_PHYSICAL)) return;

        event.setAmount(event.getAmount() * (1.5F + 0.25F * targetEffect.getAmplifier()));
    }
}
