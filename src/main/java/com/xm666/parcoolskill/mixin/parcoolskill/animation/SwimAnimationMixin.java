package com.xm666.parcoolskill.mixin.parcoolskill.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class SwimAnimationMixin {
    @Mixin(AvatarRenderer.class)
    private static class AvatarRendererMixin {
        @Inject(method = "setupRotations(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;FF)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionfc;)V", shift = At.Shift.AFTER, ordinal = 2))
        private void onMulPose(AvatarRenderState renderState, PoseStack poseStack, float bodyRot, float scale, CallbackInfo ci) {
            var swimming = renderState.isVisuallySwimming;
            var swimAmount = renderState.swimAmount;
            var yOffset = swimming ? Mth.lerp(swimAmount, 1.0, 0.0) : Mth.lerp(swimAmount, 0.0, -1.0);
            var zOffset = swimming ? Mth.lerp(swimAmount, -0.3, 0.0) : Mth.lerp(swimAmount, 0.0, 0.3);
            poseStack.translate(0.0, yOffset, zOffset);
        }
    }
}
