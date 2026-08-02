package com.xm666.parcoolskill.mixin.parcoolskill.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class SwimAnimationMixin {
    @Mixin(PlayerRenderer.class)
    public static class PlayerRendererMixin {
        @Inject(method = "setupRotations(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;FFFF)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionf;)V", shift = At.Shift.AFTER, ordinal = 2))
        private void onMulPose(AbstractClientPlayer player, PoseStack poseStack, float bob, float yBodyRot, float partialTick, float scale, CallbackInfo ci) {
            var swimming = player.isVisuallySwimming();
            var swimAmount = player.getSwimAmount(partialTick);
            var yOffset = swimming ? Mth.lerp(swimAmount, 1.0, 0.0) : Mth.lerp(swimAmount, 0.0, -1.0);
            var zOffset = swimming ? Mth.lerp(swimAmount, -0.3, 0.0) : Mth.lerp(swimAmount, 0.0, 0.3);
            poseStack.translate(0.0, yOffset, zOffset);
        }
    }
}
