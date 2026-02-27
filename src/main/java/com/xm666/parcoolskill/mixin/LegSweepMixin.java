package com.xm666.parcoolskill.mixin;

import com.alrex.parcool.common.action.AdditionalProperties;
import com.alrex.parcool.common.action.impl.FastRun;
import com.alrex.parcool.common.action.impl.Flipping;
import com.alrex.parcool.common.action.impl.Slide;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.xm666.parcoolskill.skill.SlideSkill;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

public class LegSweepMixin {
    @Mixin(Slide.class)
    private static class SlideMixin {
        @ModifyExpressionValue(method = "canStart", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;onGround()Z"))
        private boolean modifyOnGround(boolean original, Player player) {
            return original || Parkourability.get(player).get(Flipping.class).isDoing();
        }

        @WrapOperation(method = "canStart", at = @At(value = "INVOKE", target = "Lcom/alrex/parcool/common/action/impl/FastRun;getDashTick(Lcom/alrex/parcool/common/action/AdditionalProperties;)I"))
        private int wrapDashTick(FastRun instance, AdditionalProperties properties, Operation<Integer> original, Player player) {
            return Parkourability.get(player).get(Flipping.class).isDoing() ? Parkourability.get(player).get(Slide.class).getNotDoingTick() : original.call(instance, properties);
        }

        @SuppressWarnings("ConstantValue")
        @WrapMethod(method = "onWorkingTickInLocalClient")
        private void wrapWorkingTickLocal(Player player, Parkourability parkourability, Operation<Void> original) {
            if (((SlideSkill) this).parcoolskill$isDisableSliding() && ((Slide) (Object) this).getDoingTick() >= 5)
                return;

            original.call(player, parkourability);
        }

        @ModifyArg(method = "onWorkingTickInLocalClient", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;scale(D)Lnet/minecraft/world/phys/Vec3;", ordinal = 0))
        private double modifySpeedScale(double factor) {
            var slideSkill = ((SlideSkill) this);
            if (slideSkill.parcoolskill$isDisableSliding() && slideSkill.parcoolskill$getReadyType() == SlideSkill.Type.NONE)
                return -factor;

            return factor;
        }
    }
}
