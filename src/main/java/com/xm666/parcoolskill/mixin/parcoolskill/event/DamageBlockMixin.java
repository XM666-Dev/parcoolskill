package com.xm666.parcoolskill.mixin.parcoolskill.event;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.xm666.parcoolskill.event.DamageBlockEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

public class DamageBlockMixin {
    @Mixin(LivingEntity.class)
    private static class LivingEntityMixin {
        @WrapOperation(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;applyItemBlocking(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)F"))
        private float wrapItemBlocking(LivingEntity instance, ServerLevel level, DamageSource damageSource, float damageAmount, Operation<Float> original) {
            var living = (LivingEntity) (Object) this;
            var event = new DamageBlockEvent(living, living.damageContainers.peek(), living.damageContainers.peek().getNewDamage(), false);
            NeoForge.EVENT_BUS.post(event);

            var value = original.call(instance, level, damageSource, damageAmount);
            return event.getBlocked() ? event.getBlockedDamage() : value;
        }
    }
}
