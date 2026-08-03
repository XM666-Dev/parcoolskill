package com.xm666.parcoolskill.mixin.parcoolskill.event;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.xm666.parcoolskill.event.PlayerAttackEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class PlayerAttackMixin {
    @Mixin(Player.class)
    private static class PlayerMixin {
        @ModifyExpressionValue(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/common/ForgeHooks;getCriticalHit(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;ZF)Lnet/minecraftforge/event/entity/player/CriticalHitEvent;"))
        private CriticalHitEvent modifyCriticalHit(CriticalHitEvent critEvent, Entity target, @Share("disableCrit") LocalBooleanRef disableCrit) {
            if (critEvent == null) {
                var player = (Player) (Object) this;
                critEvent = new CriticalHitEvent(player, target, 1.0F, false);
            }
            var playerAttackEvent = new PlayerAttackEvent.Pre(critEvent);
            MinecraftForge.EVENT_BUS.post(playerAttackEvent);
            if (!playerAttackEvent.isCriticalHit()) return null;

            critEvent.setDamageModifier(playerAttackEvent.getDamageMultiplier());
            try {
                var field = CriticalHitEvent.class.getDeclaredField("vanillaCritical");
                field.setAccessible(true);
                field.set(critEvent, true);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            disableCrit.set(playerAttackEvent.disableCrit());
            return critEvent;
        }

        @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", shift = At.Shift.AFTER))
        private void onHurt(Entity target, CallbackInfo ci, @Local CriticalHitEvent critEvent, @Share("disableCrit") LocalBooleanRef disableCrit) {
            if (critEvent == null) {
                var player = (Player) (Object) this;
                critEvent = new CriticalHitEvent(player, target, 1.0F, false);
            }
            var playerAttackEvent = new PlayerAttackEvent.Post(critEvent);
            playerAttackEvent.setDisableCrit(disableCrit.get());
            MinecraftForge.EVENT_BUS.post(playerAttackEvent);

            disableCrit.set(playerAttackEvent.disableCrit());
        }

        @WrapWithCondition(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;crit(Lnet/minecraft/world/entity/Entity;)V"))
        private boolean wrapCrit(Player instance, Entity entityHit, @Share("disableCrit") LocalBooleanRef disableCrit) {
            return !disableCrit.get();
        }
    }
}
