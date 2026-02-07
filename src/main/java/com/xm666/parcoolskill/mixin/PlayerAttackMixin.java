package com.xm666.parcoolskill.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.xm666.parcoolskill.event.PlayerAttackEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

public class PlayerAttackMixin {
    @Mixin(Player.class)
    private static class PlayerMixin {
        @WrapOperation(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
        private boolean wrapHurt(Entity target, DamageSource source, float amount, Operation<Boolean> original, @Local CriticalHitEvent critEvent) {
            var player = (Player) (Object) this;
            var livingTarget = target instanceof LivingEntity ? (LivingEntity) target : null;
            if (livingTarget != null) {
                var playerAttackEvent = new PlayerAttackEvent.Pre(player, livingTarget, critEvent, amount);
                NeoForge.EVENT_BUS.post(playerAttackEvent);
                amount = playerAttackEvent.getAmount();
            }
            var hurt = original.call(target, source, amount);
            if (livingTarget != null) {
                var playerAttackEvent = new PlayerAttackEvent.Post(player, livingTarget, critEvent, amount);
                NeoForge.EVENT_BUS.post(playerAttackEvent);
            }
            return hurt;
        }
    }
}
