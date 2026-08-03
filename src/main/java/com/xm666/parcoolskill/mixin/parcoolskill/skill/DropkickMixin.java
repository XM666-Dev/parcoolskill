package com.xm666.parcoolskill.mixin.parcoolskill.skill;

import com.alrex.parcool.common.action.AdditionalProperties;
import com.alrex.parcool.common.action.impl.CatLeap;
import com.alrex.parcool.common.action.impl.FastRun;
import com.alrex.parcool.common.action.impl.Slide;
import com.alrex.parcool.common.capability.Parkourability;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@OnlyIn(Dist.CLIENT)
public class DropkickMixin {
    @Mixin(value = Slide.class, remap = false)
    private static class SlideMixin {
        @ModifyExpressionValue(method = "canStart", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;onGround()Z"))
        private boolean modifyOnGround(boolean original, Player player) {
            if (original) return true;

            var parkourability = Parkourability.get(player);
            return parkourability.get(CatLeap.class).isDoing();
        }

        @WrapOperation(method = "canStart", at = @At(value = "INVOKE", target = "Lcom/alrex/parcool/common/action/impl/FastRun;getDashTick(Lcom/alrex/parcool/common/action/AdditionalProperties;)I"))
        private int wrapDashTick(FastRun instance, AdditionalProperties properties, Operation<Integer> original, Player player) {
            var parkourability = Parkourability.get(player);
            return parkourability.get(CatLeap.class).isDoing() ? parkourability.get(Slide.class).getNotDoingTick() : original.call(instance, properties);
        }

        @WrapOperation(method = "onWorkingTickInLocalClient", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;scale(D)Lnet/minecraft/world/phys/Vec3;", ordinal = 1))
        private Vec3 wrapVecScale(Vec3 instance, double factor, Operation<Vec3> original, Player player) {
            var parkourability = Parkourability.get(player);
            return parkourability.get(CatLeap.class).isDoing() ? instance : original.call(instance, factor);
        }

        @ModifyExpressionValue(method = "onWorkingTickInLocalClient", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;y()D"))
        private double modifyMovementY(double original, Player player) {
            var parkourability = Parkourability.get(player);
            return parkourability.get(CatLeap.class).isDoing() ? Math.max(original * 0.9, original) : original;
        }
    }
}
