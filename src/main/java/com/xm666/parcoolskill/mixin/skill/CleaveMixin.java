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
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.CommonHooks;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
public class CleaveMixin {
    @Mixin(Minecraft.class)
    private static class MinecraftMixin {
        @Inject(method = "handleKeybinds", at = @At("HEAD"))
        private void onHandleKeybinds(CallbackInfo ci) {
            var mc = Minecraft.getInstance();
            var player = mc.player;
            if (player == null || !CleaveHandler.isAttacking(player)) return;

            var range = PickHandler.getHitRange(player, player.entityInteractionRange());
            var count = CleaveHandler.getHitCount(player);
            var parkourability = Parkourability.get(player);
            var jumpSkill = (JumpSkill) parkourability.get(ChargeJump.class);
            var targets = PickHandler.getHitEntities(player, range, count)
                    .filter(jumpSkill::parcoolskill$addEntityHit)
                    .toArray(Entity[]::new);
            for (var target : targets) {
                player.attackStrengthTicker = (int) player.getCurrentItemAttackStrengthDelay();
                SkillHandler.use(SkillPayload.Type.CLEAVE_ATTACK, player, target);
                CommonHooks.fireCriticalHit(player, target, false, 1.0F);
                player.resetAttackStrengthTicker();
            }
        }
    }

    @Mixin(ItemInHandRenderer.class)
    private static class ItemInHandRendererMixin {
        @Inject(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;applyItemArmTransform(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/entity/HumanoidArm;F)V", shift = At.Shift.AFTER, ordinal = 4))
        private void onApplyItemArmTransform(AbstractClientPlayer player, float partialTick, float pitch, InteractionHand hand, float swingProgress, ItemStack item, float equippedProgress, PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, CallbackInfo ci) {
            if (!ClientConfig.CLEAVE_ANIMATION_ENABLED.get() || !CleaveHandler.isCharging(player)) return;

            var cleaveChargeDuration = Config.CLEAVE_CHARGE_DURATION.get();
            var parkourability = Parkourability.get(player);
            var jump = parkourability.get(ChargeJump.class);
            var finalPartialTick = jump.getNotChargingTick() == 0 ? partialTick : -partialTick;
            var tick = jump.getChargingTick() + CleaveHandler.animationTick;
            CleaveHandler.ARM_ANIMATION.apply(player, finalPartialTick, hand, poseStack, tick, cleaveChargeDuration);
        }
    }

    @Mixin(ChargeJump.class)
    private static class ChargeJumpMixin {
        @Inject(method = "onClientTick", at = @At(value = "FIELD", target = "Lcom/alrex/parcool/common/action/impl/ChargeJump;chargeTick:I", opcode = Opcodes.PUTFIELD, ordinal = 1))
        private void onChargeDoing(Player player, Parkourability parkourability, CallbackInfo ci) {
            ++CleaveHandler.animationTick;
        }

        @Inject(method = "onClientTick", at = @At(value = "FIELD", target = "Lcom/alrex/parcool/common/action/impl/ChargeJump;chargeTick:I", opcode = Opcodes.PUTFIELD, ordinal = 3))
        private void onChargeNotDoing(Player player, Parkourability parkourability, CallbackInfo ci) {
            CleaveHandler.animationTick = 0;
        }

        @Inject(method = "onClientTick", at = @At(value = "FIELD", target = "Lcom/alrex/parcool/common/action/impl/ChargeJump;chargeTick:I", opcode = Opcodes.PUTFIELD, ordinal = 4))
        private void onChargeFinish(Player player, Parkourability parkourability, CallbackInfo ci) {
            CleaveHandler.animationTick = 0;
        }
    }
}
