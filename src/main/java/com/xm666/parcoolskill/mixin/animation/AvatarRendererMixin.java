package com.xm666.parcoolskill.mixin.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public class AvatarRendererMixin {
    @Inject(method = "setupRotations(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;FF)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionfc;)V", ordinal = 2, shift = At.Shift.AFTER))
    private void onMulPose(AvatarRenderState renderState, PoseStack poseStack, float bodyRot, float scale, CallbackInfo ci) {
        var swimAmount = renderState.swimAmount;
        var swimming = renderState.isVisuallySwimming;
        var yOffset = swimming ? Mth.lerp(swimAmount, 1.0, 0.0) : Mth.lerp(swimAmount, 0.0, -1.0);
        var zOffset = swimming ? Mth.lerp(swimAmount, -0.3, 0.0) : Mth.lerp(swimAmount, 0.0, 0.3);
        poseStack.translate(0.0, yOffset, zOffset);
    }
}
