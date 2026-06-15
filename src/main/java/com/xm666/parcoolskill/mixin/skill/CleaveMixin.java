package com.xm666.parcoolskill.mixin.skill;

import com.alrex.parcool.common.action.impl.ChargeJump;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.mojang.blaze3d.vertex.PoseStack;
import com.xm666.parcoolskill.ClientConfig;
import com.xm666.parcoolskill.Config;
import com.xm666.parcoolskill.handler.PickHandler;
import com.xm666.parcoolskill.handler.SkillHandler;
import com.xm666.parcoolskill.network.SkillPayload;
import com.xm666.parcoolskill.skill.JumpSkill;
import com.xm666.parcoolskill.skill.handler.CleaveHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.stream.Stream;

@OnlyIn(Dist.CLIENT)
public class CleaveMixin {
    @Mixin(Minecraft.class)
    private static class MinecraftMixin {
        @Inject(method = "handleKeybinds", at = @At("HEAD"))
        private void onHandleKeybinds(CallbackInfo ci) {
            var mc = Minecraft.getInstance();
            var player = mc.player;
            if (player == null || !CleaveHandler.isAttacking(player)) return;

            var cleavePickRadius = Config.CLEAVE_PICK_RADIUS.get();
            var range = PickHandler.getEntityPickRange(player, player.entityInteractionRange());
            var count = CleaveHandler.getPickCount(player);
            var jumpSkill = (JumpSkill) Parkourability.get(player).get(ChargeJump.class);
            var targets = Stream.concat(
                            Arrays.stream(PickHandler.getHitEntities(player, range, 0.0, count)),
                            Arrays.stream(PickHandler.getHitEntities(player, range, cleavePickRadius, 1)))
                    .filter(jumpSkill::parcoolskill$addEntityHit)
                    .toArray(Entity[]::new);
            for (var target : targets) {
                SkillHandler.use(SkillPayload.Type.CLEAVE_ATTACK, player, target);
                player.resetAttackStrengthTicker();
            }
        }
    }

    @Mixin(ItemInHandRenderer.class)
    private static class ItemInHandRendererMixin {
        @Inject(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;applyItemArmAttackTransform(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/entity/HumanoidArm;F)V", shift = At.Shift.AFTER, ordinal = 1))
        private void onApplyAnimation(AbstractClientPlayer player, float partialTick, float pitch, InteractionHand hand, float swingProgress, ItemStack stack, float equippedProgress, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, CallbackInfo ci) {
            if (!ClientConfig.CLEAVE_ANIMATION_ENABLED.get() || !CleaveHandler.isCharging(player)) return;

            var cleaveChargeDuration = Config.CLEAVE_CHARGE_DURATION.get();
            var jump = Parkourability.get(player).get(ChargeJump.class);
            var finalPartialTick = jump.getNotChargingTick() == 0 ? partialTick : -partialTick;
            var tick = jump.getChargingTick() + CleaveHandler.getAnimationOffset();
            CleaveHandler.HAND_ANIMATION.apply(player, finalPartialTick, hand, poseStack, tick, cleaveChargeDuration);
        }
    }

    @Mixin(ChargeJump.class)
    private static class ChargeJumpMixin {
        @Inject(method = "onClientTick", at = @At(value = "FIELD", target = "Lcom/alrex/parcool/common/action/impl/ChargeJump;chargeTick:I", opcode = Opcodes.PUTFIELD, ordinal = 1))
        private void onChargeDoing(Player player, Parkourability parkourability, CallbackInfo ci) {
            CleaveHandler.addAnimationOffset();
        }

        @Inject(method = "onClientTick", at = @At(value = "FIELD", target = "Lcom/alrex/parcool/common/action/impl/ChargeJump;chargeTick:I", opcode = Opcodes.PUTFIELD, ordinal = 3))
        private void onChargeNotDoing(Player player, Parkourability parkourability, CallbackInfo ci) {
            CleaveHandler.clearAnimationOffset();
        }

        @Inject(method = "onClientTick", at = @At(value = "FIELD", target = "Lcom/alrex/parcool/common/action/impl/ChargeJump;chargeTick:I", opcode = Opcodes.PUTFIELD, ordinal = 4))
        private void onChargeFinish(Player player, Parkourability parkourability, CallbackInfo ci) {
            CleaveHandler.clearAnimationOffset();
        }
    }
}
