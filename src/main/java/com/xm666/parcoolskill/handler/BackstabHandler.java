package com.xm666.parcoolskill.handler;

import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import com.alrex.parcool.common.action.impl.Dodge;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.xm666.parcoolskill.ParCoolSkill;
import com.xm666.parcoolskill.action.BackstabDodge;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber(modid = ParCoolSkill.MODID)
public class BackstabHandler {
    @SubscribeEvent
    static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        var source = event.getSource();
        var sourceEntity = source.getEntity();
        if (sourceEntity instanceof Player player && source.is(DamageTypes.PLAYER_ATTACK)) {
            var dodge = (BackstabDodge) Parkourability.get(player).get(Dodge.class);
            if (!dodge.parcoolskill$isQueueAttack()) return;
            var sourcePosition = source.getSourcePosition();
            if (sourcePosition == null) return;
            var targetEntity = event.getEntity();
            var targetEyePosition = targetEntity.getEyePosition(1.0F);
            var targetViewVector = targetEntity.getViewVector(1.0F);
            var sourcePositionDifference = sourcePosition.subtract(targetEyePosition);
            var forward = new Vec2((float) targetViewVector.x, (float) targetViewVector.z);
            var target = new Vec2((float) sourcePositionDifference.x, (float) sourcePositionDifference.z);
            if (!isBehind(forward, target)) return;
            event.setAmount(event.getAmount() * 2.0F);
            player.magicCrit(targetEntity);
            dodge.parcoolskill$setQueueAttack(false);
        }
    }

    static boolean isBehind(Vec2 forward, Vec2 target) {
        forward = forward.normalized();
        target = target.normalized();
        float dot = forward.dot(target);
        return dot < 0;
    }

    @SubscribeEvent
    static void onDodgeStart(ParCoolActionEvent.StartEvent event) {
        if (!(event.getAction() instanceof BackstabDodge dodge)) return;
        dodge.parcoolskill$setQueueAttack(true);
    }

    @SubscribeEvent
    static void onDodgeStop(ParCoolActionEvent.StopEvent event) {
        if (!(event.getAction() instanceof BackstabDodge dodge)) return;
        dodge.parcoolskill$setQueueAttack(false);
    }
}
