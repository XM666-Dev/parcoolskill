package com.xm666.parcoolskill.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector3f;

public record ArmAnimation(Vector3f position, Vector3f rotation, float offset, boolean ease) {
    public void apply(Player player, float partialTick, InteractionHand hand, PoseStack poseStack, int tick, int duration) {
        var arm = hand == InteractionHand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
        var direction = arm == HumanoidArm.RIGHT ? 1.0F : -1.0F;
        poseStack.translate(direction * position.x, position.y, position.z);
        poseStack.mulPose(Axis.XP.rotationDegrees(rotation.x));
        poseStack.mulPose(Axis.YP.rotationDegrees(direction * rotation.y));
        poseStack.mulPose(Axis.ZP.rotationDegrees(direction * rotation.z));

        var progressTick = tick + partialTick - 1.0F;
        var progressAmount = progressTick / duration;
        if (ease) {
            progressAmount = (progressAmount * progressAmount + progressAmount * 2.0F) / 3.0F;
        }
        progressAmount = Math.min(progressAmount, 1.0F);

        if (progressAmount > 0.1F) {
            var wobbleAmount = Mth.sin(progressTick * 1.3F - 0.13F) * (progressAmount - 0.1F) * 0.004F;
            poseStack.translate(0.0F, wobbleAmount, 0.0F);
        }

        poseStack.translate(0.0F, 0.0F, progressAmount * offset);
        poseStack.scale(1.0F, 1.0F, 1.0F + progressAmount * 0.2F);
        poseStack.mulPose(Axis.YN.rotationDegrees(direction * 45.0F));
    }
}
