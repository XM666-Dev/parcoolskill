package com.xm666.parcoolskill.mixin;

import com.alrex.parcool.common.action.BehaviorEnforcer;
import com.alrex.parcool.common.action.impl.Dodge;
import com.alrex.parcool.common.action.impl.Flipping;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.xm666.parcoolskill.Config;
import com.xm666.parcoolskill.skill.FlippingSkill;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
public class FlickFlackMixin {
    @Mixin(Dodge.class)
    private static class DodgeMixin {
        @ModifyArg(method = "onStartInLocalClient", at = @At(value = "INVOKE", target = "Lcom/alrex/parcool/common/action/BehaviorEnforcer;addMarkerCancellingJump(Lcom/alrex/parcool/common/action/BehaviorEnforcer$ID;Lcom/alrex/parcool/common/action/BehaviorEnforcer$Marker;)V"), index = 1)
        private BehaviorEnforcer.Marker modifyJumpCancelMarker(BehaviorEnforcer.Marker marker) {
            var control = ParCoolConfig.Client.getInstance().FlipControl.get();
            var mc = Minecraft.getInstance();
            return () -> marker.remain() && !(control.isInputDone(false) && mc.hitResult instanceof EntityHitResult || !Config.FLICK_FLACK_ENABLED.get());
        }
    }

    @Mixin(ItemInHandRenderer.class)
    private static class ItemInHandRendererMixin {
        @Inject(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;applyItemArmAttackTransform(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/entity/HumanoidArm;F)V", shift = At.Shift.AFTER, ordinal = 1))
        private void onApplyTransform(AbstractClientPlayer player, float partialTicks, float pitch, InteractionHand hand, float swingProgress, ItemStack stack, float equippedProgress, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, CallbackInfo ci) {
            var flipping = Parkourability.get(player).get(Flipping.class);
            var flippingSkill = (FlippingSkill) flipping;
            if (!flippingSkill.parcoolskill$isAttackReady() || !Config.FLICK_FLACK_ANIMATION_ENABLED.get()) return;

            var arm = hand == InteractionHand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
            var direction = arm == HumanoidArm.RIGHT ? 1 : -1;
            poseStack.translate(direction * -0.25F, 0.35F, 0.05F);
            poseStack.mulPose(Axis.XP.rotationDegrees(-55.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(direction * 35.3F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(direction * -9.785F));
            var progressTicks = flipping.getDoingTick() + partialTicks - 1.0F;
            var progressAmount = progressTicks / 10.0F;
            if (progressAmount > 1.0F) {
                progressAmount = 1.0F;
            }
            if (progressAmount > 0.1F) {
                var sinned = Mth.sin((progressTicks - 0.1F) * 1.3F);
                var offsetAmount = progressAmount - 0.1F;
                var smoothAmount = sinned * offsetAmount;
                poseStack.translate(0.0F, smoothAmount * 0.004F, 0.0F);
            }
            poseStack.translate(0.0F, 0.0F, progressAmount * 0.2F);
            poseStack.scale(1.0F, 1.0F, 1.0F + progressAmount * 0.2F);
            poseStack.mulPose(Axis.YN.rotationDegrees(direction * 45.0F));
        }
    }
}
