package com.xm666.parcoolskill.mixin.parcoolskill.skill;

import com.alrex.parcool.common.action.BehaviorEnforcer;
import com.alrex.parcool.common.action.impl.Dodge;
import com.alrex.parcool.common.action.impl.Flipping;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.xm666.parcoolskill.ClientConfig;
import com.xm666.parcoolskill.Config;
import com.xm666.parcoolskill.skill.FlippingSkill;
import com.xm666.parcoolskill.skill.handler.FlickFlackHandler;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class FlickFlackMixin {
    @OnlyIn(Dist.CLIENT)
    @Mixin(Dodge.class)
    private static class DodgeMixin {
        @ModifyArg(method = "onStartInLocalClient", at = @At(value = "INVOKE", target = "Lcom/alrex/parcool/common/action/BehaviorEnforcer;addMarkerCancellingJump(Lcom/alrex/parcool/common/action/BehaviorEnforcer$ID;Lcom/alrex/parcool/common/action/BehaviorEnforcer$Marker;)V"), index = 1)
        private BehaviorEnforcer.Marker modifyJumpCancelMarker(BehaviorEnforcer.Marker marker, @Local(argsOnly = true) Parkourability parkourability) {
            return () -> marker.remain() && !FlickFlackHandler.canJump(parkourability);
        }
    }

    @Mixin(Player.class)
    private static class PlayerMixin {
        @ModifyReturnValue(method = "getFlyingSpeed", at = @At("RETURN"))
        private float modifyFlyingSpeed(float original) {
            var player = (Player) (Object) this;
            var parkourability = Parkourability.get(player);
            var flipping = parkourability.get(Flipping.class);
            var flippingSkill = (FlippingSkill) flipping;
            if (!flippingSkill.parcoolskill$isAccelerated()) return original;

            return original * (1.0F + Config.FLICK_FLACK_SPEED_MULTIPLIER_ADDITION.get().floatValue());
        }
    }

    @Mixin(LivingEntity.class)
    private static class LivingEntityMixin {
        @ModifyExpressionValue(method = "applyItemBlocking", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getItemBlockingWith()Lnet/minecraft/world/item/ItemStack;"))
        private ItemStack modifyItemBlockingWith(ItemStack original) {
            if (original != null || !((Object) this instanceof Player player)) return original;

            var parkourability = Parkourability.get(player);
            var flippingSkill = (FlippingSkill) parkourability.get(Flipping.class);
            if (flippingSkill.parcoolskill$getParryTime() == 0) return null;

            return player.getWeaponItem();
        }

        @ModifyExpressionValue(method = "applyItemBlocking", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;"))
        private Object modifyBlocksAttacks(Object original) {
            if (original != null || !((Object) this instanceof Player player)) return original;

            var parkourability = Parkourability.get(player);
            var flippingSkill = (FlippingSkill) parkourability.get(Flipping.class);
            if (flippingSkill.parcoolskill$getParryTime() == 0) return null;

            return Items.SHIELD.components().get(DataComponents.BLOCKS_ATTACKS);
        }

        @ModifyExpressionValue(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;"))
        private Object wrapBlocksAttacks(Object original) {
            if (original != null || !((Object) this instanceof Player player)) return original;

            var parkourability = Parkourability.get(player);
            var flippingSkill = (FlippingSkill) parkourability.get(Flipping.class);
            if (flippingSkill.parcoolskill$getParryTime() == 0) return null;

            return Items.SHIELD.components().get(DataComponents.BLOCKS_ATTACKS);
        }
    }

    @Mixin(ItemInHandRenderer.class)
    private static class ItemInHandRendererMixin {
        @Inject(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;applyItemArmTransform(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/entity/HumanoidArm;F)V", shift = At.Shift.AFTER, ordinal = 4))
        private void onApplyItemArmTransform(AbstractClientPlayer player, float partialTick, float pitch, InteractionHand hand, float swingProgress, ItemStack item, float equippedProgress, PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, CallbackInfo ci) {
            if (!ClientConfig.FLICK_FLACK_ANIMATION_ENABLED.get() || hand != InteractionHand.MAIN_HAND) return;

            var parkourability = Parkourability.get(player);
            var flipping = parkourability.get(Flipping.class);
            var flippingSkill = (FlippingSkill) flipping;
            if (!flippingSkill.parcoolskill$isAttackReady()) return;

            var tick = flipping.getDoingTick();
            FlickFlackHandler.ARM_ANIMATION.apply(player, partialTick, hand, poseStack, tick, 10);
        }
    }
}
