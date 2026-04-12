package com.xm666.parcoolskill.mixin;

import com.alrex.parcool.common.action.AdditionalProperties;
import com.alrex.parcool.common.action.impl.CatLeap;
import com.alrex.parcool.common.action.impl.FastRun;
import com.alrex.parcool.common.action.impl.Slide;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.xm666.parcoolskill.Config;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

public class DropkickMixin {
    @Mixin(Slide.class)
    private static class SlideMixin {
        @ModifyExpressionValue(method = "canStart", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;onGround()Z"))
        private boolean modifyOnGround(boolean original, Player player) {
            return original || Parkourability.get(player).get(CatLeap.class).isDoing() && Config.DROPKICK_ENABLED.get();
        }

        @WrapOperation(method = "canStart", at = @At(value = "INVOKE", target = "Lcom/alrex/parcool/common/action/impl/FastRun;getDashTick(Lcom/alrex/parcool/common/action/AdditionalProperties;)I"))
        private int wrapDashTick(FastRun instance, AdditionalProperties properties, Operation<Integer> original, Player player) {
            return Parkourability.get(player).get(CatLeap.class).isDoing() ? Parkourability.get(player).get(Slide.class).getNotDoingTick() : original.call(instance, properties);
        }

        @WrapOperation(method = "onWorkingTickInLocalClient", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;scale(D)Lnet/minecraft/world/phys/Vec3;", ordinal = 1))
        private Vec3 wrapVecScale(Vec3 instance, double factor, Operation<Vec3> original, Player player) {
            return Parkourability.get(player).get(CatLeap.class).isDoing() ? instance : original.call(instance, factor);
        }

        @ModifyExpressionValue(method = "onWorkingTickInLocalClient", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;y()D"))
        private double modifyMovementY(double original, Player player) {
            return Parkourability.get(player).get(CatLeap.class).isDoing() ? Math.max(original * 0.9, original) : original;
        }
    }
}
