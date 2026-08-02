package com.xm666.parcoolskill.mixin.epicparcool;

import com.yesman.epicparcool.animations.ParCoolAnimations;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StaticAnimation;

import java.util.function.Function;

public class AnimationsMixin {
    @Mixin(value = ParCoolAnimations.class,remap = false)
    private static class ParCoolAnimationsMixin {
        @ModifyArg(method = "build", at = @At(value = "INVOKE", target = "Lyesman/epicfight/api/animation/AnimationManager$AnimationBuilder;nextAccessor(Ljava/lang/String;Ljava/util/function/Function;)Lyesman/epicfight/api/animation/AnimationManager$AnimationAccessor;"))
        private static <T extends StaticAnimation> Function<AnimationManager.AnimationAccessor<T>, T> wrapBuild(Function<AnimationManager.AnimationAccessor<T>, T> onLoad) {
            return onLoad.andThen(animation -> animation
                    .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, true)
                    .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, true)
                    .addStateRemoveOld(EntityState.CAN_USE_ITEM, true)
            );
        }
    }
}
