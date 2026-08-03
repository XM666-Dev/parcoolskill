package com.xm666.parcoolskill.mixin.parcoolskill.event;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.xm666.parcoolskill.event.DamageBlockEvent;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

public class DamageBlockMixin {
    @Mixin(LivingEntity.class)
    private static class LivingEntityMixin {
        @ModifyExpressionValue(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isDamageSourceBlocked(Lnet/minecraft/world/damagesource/DamageSource;)Z"))
        private boolean modifyDamageSourceBlocked(boolean original) {
            var living = (LivingEntity) (Object) this;
            var event = new DamageBlockEvent(living, living.damageContainers.peek(), living.damageContainers.peek().getNewDamage(), original);
            NeoForge.EVENT_BUS.post(event);

            return original || event.getBlocked();
        }
    }
}
