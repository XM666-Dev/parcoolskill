package com.xm666.parcoolskill.mixin.parcoolskill.event;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.xm666.parcoolskill.event.DamageBlockEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

public class DamageBlockMixin {
    @Mixin(LivingEntity.class)
    private static class LivingEntityMixin {
        @WrapOperation(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isDamageSourceBlocked(Lnet/minecraft/world/damagesource/DamageSource;)Z"))
        private boolean wrapDamageSourceBlocked(LivingEntity instance, DamageSource damageSource, Operation<Boolean> original, @Local(argsOnly = true) float amount) {
            var living = (LivingEntity) (Object) this;
            var event = new DamageBlockEvent(living, damageSource, amount, false);
            MinecraftForge.EVENT_BUS.post(event);

            var value = original.call(instance, damageSource);
            return event.getBlocked() || value;
        }
    }
}
