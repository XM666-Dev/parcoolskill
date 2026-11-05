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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

public class MeleeAttackMixin {
    @Mixin(IMeleeWeapon.class)
    private interface IMeleeWeaponMixin {
        @ModifyExpressionValue(method = "performAttack", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/common/ForgeHooks;getCriticalHit(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;ZF)Lnet/minecraftforge/event/entity/player/CriticalHitEvent;", remap = false))
        private CriticalHitEvent modifyCriticalHit(CriticalHitEvent critEvent, Player player, Entity target, @Share("disableCrit") LocalBooleanRef disableCrit, @Share("disableSweep") LocalBooleanRef disableSweep) {
            if (critEvent == null) {
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
            disableSweep.set(playerAttackEvent.disableSweep());
            return critEvent;
        }

        @WrapOperation(method = "performAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
        private boolean onHurt(Entity instance, DamageSource source, float amount, Operation<Boolean> original, Player player, Entity target, @Local(name = "hitResult") CriticalHitEvent critEvent, @Share("disableCrit") LocalBooleanRef disableCrit) {
            var flag = original.call(instance, source, amount);
            if (critEvent == null) {
                critEvent = new CriticalHitEvent(player, instance, 1.0F, false);
            }
            var playerAttackEvent = new PlayerAttackEvent.Post(critEvent, source);
            playerAttackEvent.setDisableCrit(disableCrit.get());
            MinecraftForge.EVENT_BUS.post(playerAttackEvent);

            disableCrit.set(playerAttackEvent.disableCrit());
            return flag;
        }

        @WrapWithCondition(method = "performAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;crit(Lnet/minecraft/world/entity/Entity;)V"))
        private boolean wrapCrit(Player instance, Entity entityHit, @Share("disableCrit") LocalBooleanRef disableCrit) {
            return !disableCrit.get();
        }
    }
}
