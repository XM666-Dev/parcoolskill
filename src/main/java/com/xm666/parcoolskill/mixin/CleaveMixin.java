package com.xm666.parcoolskill.mixin;

import com.alrex.parcool.common.action.impl.ChargeJump;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.xm666.parcoolskill.action.SkillJump;
import com.xm666.parcoolskill.handler.CleaveHandler;
import com.xm666.parcoolskill.handler.StaminaHandler;
import com.xm666.parcoolskill.network.StopChargePayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class CleaveMixin {
    @Mixin(Player.class)
    private static class PlayerMixin {
        @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
        private void injectAttack(Entity target, CallbackInfo ci, @Local(ordinal = 3) boolean isSweeping, @Share("queueSweep") LocalBooleanRef queueSweep) {
            if (isSweeping) {
                if (!((Object) this instanceof ServerPlayer source)) return;
                var jump = (SkillJump) Parkourability.get(source).get(ChargeJump.class);
                if (jump.parcoolskill$getChargeTick() < ChargeJump.JUMP_ANIMATION_TICK) return;
                if (!StaminaHandler.consumeStamina(source, 400)) return;
                CleaveHandler.queueAttack = true;
                queueSweep.set(true);
                PacketDistributor.sendToPlayer(source, StopChargePayload.INSTANCE);
            }
        }

        @ModifyExpressionValue(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getAttributeValue(Lnet/minecraft/core/Holder;)D", ordinal = 1))
        private double modifySweepingDamageRatio(double original, @Share("queueSweep") LocalBooleanRef queueSweep) {
            return queueSweep.get() ? original + 1.25F : original;
        }

        @ModifyExpressionValue(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getSweepHitBox(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/phys/AABB;"))
        private AABB modifySweepHitBox(AABB original, @Share("queueSweep") LocalBooleanRef queueSweep) {
            if (queueSweep.get()) {
                var scale = CleaveHandler.getCleaveScale();
                var multiplier = scale * 0.5 - 0.5;
                var width = (original.maxX - original.minX) * multiplier;
                var height = (original.maxY - original.minY) * multiplier;
                var depth = (original.maxZ - original.minZ) * multiplier;
                original = new AABB(original.minX - width, original.minY - height, original.minZ - depth, original.maxX + width, original.maxY + height, original.maxZ + depth);
            }
            return original;
        }

        @ModifyExpressionValue(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;entityInteractionRange()D"))
        private double modifyEntityInteractionRange(double original, @Share("queueSweep") LocalBooleanRef queueSweep) {
            return queueSweep.get() ? original * 2.0 : original;
        }
    }
}
