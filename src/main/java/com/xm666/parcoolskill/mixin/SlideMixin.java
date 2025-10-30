package com.xm666.parcoolskill.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.xm666.parcoolskill.handler.SlideHandler;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class SlideMixin {
    @Mixin(PlayerRenderer.class)
    private static class PlayerRendererMixin {
        @ModifyExpressionValue(method = "setupRotations(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;FFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;getSwimAmount(F)F"))
        private float modifySwimAmount(float original) {
            return SlideHandler.flipPose ? Mth.map(original, 1.0F, 0.0F, -0.7F, 0.0F) : original;
        }

        @Definition(id = "f", local = @Local(type = float.class, ordinal = 4))
        @Expression("f > 0.0")
        @ModifyExpressionValue(method = "setupRotations(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;FFFF)V", at = @At(value = "MIXINEXTRAS:EXPRESSION"))
        private boolean modifySwimCheck(boolean original) {
            return original || SlideHandler.flipPose;
        }

        @Inject(method = "setupRotations(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;FFFF)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionf;)V", ordinal = 2))
        private void injectPose(AbstractClientPlayer entity, PoseStack poseStack, float bob, float yBodyRot, float partialTick, float scale, CallbackInfo ci) {
            if (SlideHandler.flipPose) {
                poseStack.translate(0.0F, 0.0F, Mth.map(entity.getSwimAmount(partialTick), 1.0F, 0.0F, -0.6F, 0.0F));
            }
        }

        //爬行恢复：在滑铲结束需要继续爬行时，将amount设为零并进行重映射，以进行平滑的过渡
        //目前不予实现
    }
}
