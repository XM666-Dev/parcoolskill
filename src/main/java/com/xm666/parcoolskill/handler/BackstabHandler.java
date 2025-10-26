package com.xm666.parcoolskill.handler;

import com.xm666.parcoolskill.ParCoolSkill;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber(modid = ParCoolSkill.MODID)
public class BackstabHandler {
    public static boolean queueAttack;

    @SubscribeEvent
    static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        var source = event.getSource();
        var sourceEntity = source.getEntity();
        if (sourceEntity instanceof Player player && source.is(DamageTypes.PLAYER_ATTACK) && queueAttack) {
            var sourcePosition = source.getSourcePosition();
            if (sourcePosition != null) {
                var targetEntity = event.getEntity();
                //var targetRotation = targetEntity.getPreciseBodyRotation(1.0F);
                //var sourceRotation = sourceEntity.getYRot();
                var targetEyePosition = targetEntity.getEyePosition(1.0F);
                var targetViewVector = targetEntity.getViewVector(1.0F);
                var sourcePositionDifference = sourcePosition.subtract(targetEyePosition);
                var forward = new Vec2((float) targetViewVector.x, (float) targetViewVector.z);
                var target = new Vec2((float) sourcePositionDifference.x, (float) sourcePositionDifference.z);
                //flag |= Mth.degreesDifferenceAbs(targetRotation, sourceRotation) < 90.0F;
                if (isBehind(forward, target)) {
                    event.setAmount(event.getAmount() * 2.0F);
                    player.magicCrit(targetEntity);
                    queueAttack = false;
                }
            }
        }
    }

    static boolean isBehind(Vec2 forward, Vec2 target) {
        forward = forward.normalized();
        target = target.normalized();
        float dot = forward.dot(target);
        return dot < 0;
    }
}
