package com.xm666.parcoolskill.mixin.event;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.xm666.parcoolskill.event.LivingBlockEvent;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

public class LivingBlockMixin {
    @Mixin(LivingEntity.class)
    private static class LivingEntityMixin {
        @ModifyReturnValue(method = "isBlocking", at = @At("RETURN"))
        public boolean modifyBlocking(boolean original) {
            return NeoForge.EVENT_BUS.post(new LivingBlockEvent((LivingEntity) (Object) this, original)).isSuccessful();
        }
    }
}
