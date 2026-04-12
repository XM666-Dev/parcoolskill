package com.xm666.parcoolskill.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.xm666.parcoolskill.event.PlayerAttackEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class PlayerAttackMixin {
    @Mixin(Player.class)
    private static class PlayerMixin {
        @ModifyExpressionValue(method = "attack", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/common/CommonHooks;fireCriticalHit(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;ZF)Lnet/neoforged/neoforge/event/entity/player/CriticalHitEvent;"))
        private CriticalHitEvent modifyCriticalHit(CriticalHitEvent critEvent, @Share("disableCrit") LocalBooleanRef disableCrit) {
            var playerAttackEvent = new PlayerAttackEvent.Pre(
                    (Player) (Object) this,
                    critEvent.getTarget(),
                    critEvent.getVanillaMultiplier(),
                    critEvent.isVanillaCritical(),
                    critEvent.disableSweep()
            );
            playerAttackEvent.setDamageMultiplier(critEvent.getDamageMultiplier());
            playerAttackEvent.setCriticalHit(critEvent.isCriticalHit());
            NeoForge.EVENT_BUS.post(playerAttackEvent);
            critEvent.setDamageMultiplier(playerAttackEvent.getDamageMultiplier());
            critEvent.setCriticalHit(playerAttackEvent.isCriticalHit());
            critEvent.setDisableSweep(playerAttackEvent.disableSweep());
            disableCrit.set(playerAttackEvent.disableCrit());
            return critEvent;
        }

        @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", shift = At.Shift.AFTER))
        private void onHurt(Entity target, CallbackInfo ci, @Local CriticalHitEvent critEvent, @Share("disableCrit") LocalBooleanRef disableCrit) {
            var playerAttackEvent = new PlayerAttackEvent.Post(
                    (Player) (Object) this,
                    target,
                    critEvent.getVanillaMultiplier(),
                    critEvent.isVanillaCritical(),
                    critEvent.disableSweep()
            );
            playerAttackEvent.setDamageMultiplier(critEvent.getDamageMultiplier());
            playerAttackEvent.setCriticalHit(critEvent.isCriticalHit());
            playerAttackEvent.setDisableCrit(disableCrit.get());
            NeoForge.EVENT_BUS.post(playerAttackEvent);
            disableCrit.set(playerAttackEvent.disableCrit());
        }

        @WrapWithCondition(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;crit(Lnet/minecraft/world/entity/Entity;)V"))
        private boolean wrapCrit(Player instance, Entity entityHit, @Share("disableCrit") LocalBooleanRef disableCrit) {
            return !disableCrit.get();
        }
    }
}
