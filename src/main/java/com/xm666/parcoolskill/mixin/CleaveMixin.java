package com.xm666.parcoolskill.mixin;

import com.alrex.parcool.api.Stamina;
import com.alrex.parcool.common.action.impl.ChargeJump;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.xm666.parcoolskill.Config;
import com.xm666.parcoolskill.handler.CleaveHandler;
import com.xm666.parcoolskill.handler.SkillHandler;
import com.xm666.parcoolskill.network.SkillPayload;
import com.xm666.parcoolskill.skill.JumpSkill;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.stream.Stream;

public class CleaveMixin {
    @Mixin(Minecraft.class)
    private static class MinecraftMixin {
        @Inject(method = "handleKeybinds", at = @At("HEAD"))
        private void onHandleKeybinds(CallbackInfo ci) {
            var mc = Minecraft.getInstance();
            var player = mc.player;
            if (player == null || !CleaveHandler.isReadyForAttack(player)) return;

            var cleavePickCountBase = Config.CLEAVE_PICK_COUNT_BASE.get();
            var cleavePickRadius = Config.CLEAVE_PICK_RADIUS.get();
            var jumpSkill = (JumpSkill) Parkourability.get(player).get(ChargeJump.class);
            var range = SkillHandler.getEntityPickRange(player, player.entityInteractionRange());
            var sweepingEdge = player.level().registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.SWEEPING_EDGE);
            var count = cleavePickCountBase + player.getWeaponItem().getEnchantmentLevel(sweepingEdge);
            var targets = Stream.concat(
                    Arrays.stream(SkillHandler.getEntityHits(player, range, 0.0, count)),
                    Arrays.stream(SkillHandler.getEntityHits(player, range, cleavePickRadius, 1))
            ).filter(jumpSkill::parcoolskill$addEntityHit).toArray(Entity[]::new);
            for (var target : targets) {
                SkillHandler.use(SkillPayload.Type.CLEAVE_ATTACK, player, target);
                player.resetAttackStrengthTicker();
            }
        }
    }

    @Mixin(ItemInHandRenderer.class)
    private static class ItemInHandRendererMixin {
        @Inject(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;applyItemArmAttackTransform(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/entity/HumanoidArm;F)V", shift = At.Shift.AFTER, ordinal = 1))
        private void onApplyTransform(AbstractClientPlayer player, float partialTicks, float pitch, InteractionHand hand, float swingProgress, ItemStack stack, float equippedProgress, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, CallbackInfo ci) {
            if (!CleaveHandler.hasCorrectWeapon(player) || !Config.CLEAVE_ANIMATION_ENABLED.get()) return;

            var jump = Parkourability.get(player).get(ChargeJump.class);
            if (!jump.isCharging()) return;

            var stamina = Stamina.get(player);
            if (stamina.isExhausted()) return;

            var cleaveChargeDuration = Config.CLEAVE_CHARGE_DURATION.get();
            var jumpSkill = (JumpSkill) jump;
            poseStack.translate(-0.1392841F, 0.091721935F, 0.078657655F);
            poseStack.mulPose(Axis.XP.rotationDegrees(-13.935F));
            poseStack.mulPose(Axis.YP.rotationDegrees(35.3F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(-9.785F));
            var progressTicks = jump.getChargingTick() + (partialTicks - 1.0F) * (jump.getNotChargingTick() == 0 ? 1.0F : -1.0F);
            var progressAmount = progressTicks / cleaveChargeDuration;
            progressAmount = (progressAmount * progressAmount + progressAmount * 2.0F) / 3.0F;
            if (progressAmount > 1.0F) {
                progressAmount = 1.0F;
            }
            if (progressAmount > 0.1F) {
                var sinned = Mth.sin((progressTicks - 0.1F + jumpSkill.parcoolskill$getRenderTick()) * 1.3F);
                var offsetAmount = progressAmount - 0.1F;
                var smoothAmount = sinned * offsetAmount;
                poseStack.translate(0.0F, smoothAmount * 0.004F, 0.0F);
            }
            poseStack.translate(0.0F, 0.0F, progressAmount * 0.04F);
            poseStack.scale(1.0F, 1.0F, 1.0F + progressAmount * 0.2F);
            poseStack.mulPose(Axis.YN.rotationDegrees(45.0F));
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Mixin(ChargeJump.class)
    private static class ChargeJumpMixin {
        @Inject(method = "onClientTick", at = @At(value = "FIELD", target = "Lcom/alrex/parcool/common/action/impl/ChargeJump;chargeTick:I", ordinal = 1, opcode = Opcodes.PUTFIELD))
        private void onApplyThreshold(Player player, Parkourability parkourability, CallbackInfo ci) {
            var jumpSkill = (JumpSkill) this;
            jumpSkill.parcoolskill$addRenderTick();
        }
    }
}
