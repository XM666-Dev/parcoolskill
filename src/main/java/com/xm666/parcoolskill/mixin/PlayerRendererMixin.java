package com.xm666.parcoolskill.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public class PlayerRendererMixin {
    @Inject(method = "setupRotations(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;FFFF)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionf;)V", ordinal = 2, shift = At.Shift.AFTER))
    private void onMulPose(AbstractClientPlayer player, PoseStack poseStack, float bob, float yBodyRot, float partialTick, float scale, CallbackInfo ci) {
        var swimAmount = player.getSwimAmount(partialTick);
        var swimming = player.isVisuallySwimming();
        var yOffset = swimming ? Mth.lerp(swimAmount, 1.0, 0.0) : Mth.lerp(swimAmount, 0.0, -1.0);
        var zOffset = swimming ? Mth.lerp(swimAmount, -0.3, 0.0) : Mth.lerp(swimAmount, 0.0, 0.3);
        poseStack.translate(0.0, yOffset, zOffset);
    }
}
