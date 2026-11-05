package com.xm666.parcoolskill.mixin.lrtactical;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.xm666.parcoolskill.event.PlayerAttackEvent;
import me.xjqsh.lrtactical.api.item.IMeleeWeapon;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

public class MeleeAttackMixin {
    @Mixin(IMeleeWeapon.class)
    private interface IMeleeWeaponMixin {
        @ModifyExpressionValue(method = "performAttack", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/common/CommonHooks;fireCriticalHit(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;ZF)Lnet/neoforged/neoforge/event/entity/player/CriticalHitEvent;"))
        private CriticalHitEvent modifyCriticalHit(CriticalHitEvent critEvent, @Share("disableCrit") LocalBooleanRef disableCrit) {
            var playerAttackEvent = new PlayerAttackEvent.Pre(critEvent);
            NeoForge.EVENT_BUS.post(playerAttackEvent);

            critEvent.setDamageMultiplier(playerAttackEvent.getDamageMultiplier());
            critEvent.setCriticalHit(playerAttackEvent.isCriticalHit());
            critEvent.setDisableSweep(playerAttackEvent.disableSweep());
            disableCrit.set(playerAttackEvent.disableCrit());
            return critEvent;
        }

        @WrapOperation(method = "performAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
        private boolean onHurt(Entity instance, DamageSource source, float amount, Operation<Boolean> original, @Local(name = "hitResult") CriticalHitEvent critEvent, @Share("disableCrit") LocalBooleanRef disableCrit) {
            var flag = original.call(instance, source, amount);
            var playerAttackEvent = new PlayerAttackEvent.Post(critEvent, source);
            playerAttackEvent.setDisableCrit(disableCrit.get());
            NeoForge.EVENT_BUS.post(playerAttackEvent);

            disableCrit.set(playerAttackEvent.disableCrit());
            return flag;
        }

        @WrapWithCondition(method = "performAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;crit(Lnet/minecraft/world/entity/Entity;)V"))
        private boolean wrapCrit(Player instance, Entity entityHit, @Share("disableCrit") LocalBooleanRef disableCrit) {
            return !disableCrit.get();
        }
    }
}
