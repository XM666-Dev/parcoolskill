package com.xm666.parcoolskill.mixin.event;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.xm666.parcoolskill.event.PlayerAttackEvent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class PlayerAttackMixin {
    @Mixin(Player.class)
    private static abstract class PlayerMixin {
        @Shadow
        protected abstract void playServerSideSound(SoundEvent sound);

        @ModifyExpressionValue(method = "attack", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/common/CommonHooks;fireCriticalHit(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;ZF)Lnet/neoforged/neoforge/event/entity/player/CriticalHitEvent;"))
        private CriticalHitEvent modifyCriticalHit(CriticalHitEvent critEvent, @Share("disableCrit") LocalBooleanRef disableCrit) {
            var playerAttackEvent = new PlayerAttackEvent.Pre(critEvent);
            NeoForge.EVENT_BUS.post(playerAttackEvent);

            critEvent.setDamageMultiplier(playerAttackEvent.getDamageMultiplier());
            critEvent.setCriticalHit(playerAttackEvent.isCriticalHit());
            critEvent.setDisableSweep(playerAttackEvent.disableSweep());
            disableCrit.set(playerAttackEvent.disableCrit());
            return critEvent;
        }

        @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurtOrSimulate(Lnet/minecraft/world/damagesource/DamageSource;F)Z", shift = At.Shift.AFTER))
        private void onHurt(Entity target, CallbackInfo ci, @Local CriticalHitEvent critEvent, @Share("disableCrit") LocalBooleanRef disableCrit) {
            var playerAttackEvent = new PlayerAttackEvent.Post(critEvent);
            playerAttackEvent.setDisableCrit(disableCrit.get());
            NeoForge.EVENT_BUS.post(playerAttackEvent);

            disableCrit.set(playerAttackEvent.disableCrit());
        }

        @ModifyArg(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;attackVisualEffects(Lnet/minecraft/world/entity/Entity;ZZZZF)V"), index = 1)
        private boolean modifyCrit(boolean crit, @Share("disableCrit") LocalBooleanRef disableCrit) {
            if (!crit) return false;

            if (!disableCrit.get()) return true;

            playServerSideSound(SoundEvents.PLAYER_ATTACK_CRIT);
            return false;
        }
    }
}
