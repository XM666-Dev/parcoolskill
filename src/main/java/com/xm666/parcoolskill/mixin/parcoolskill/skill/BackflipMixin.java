package com.xm666.parcoolskill.mixin.parcoolskill.skill;

import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.ActionProcessor;
import com.alrex.parcool.common.action.impl.Flipping;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.xm666.parcoolskill.Config;
import com.xm666.parcoolskill.skill.FlippingSkill;
import com.xm666.parcoolskill.skill.handler.BackflipHandler;
import com.xm666.timescalelib.handler.TimeScaleHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.ByteBuffer;

public class BackflipMixin {
    @Mixin(Flipping.class)
    private static class FlippingMixin {
        @OnlyIn(Dist.CLIENT)
        @ModifyExpressionValue(method = "canStart", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isShiftKeyDown()Z"))
        private boolean modifyShiftKeyDown(boolean original, @Local(argsOnly = true) Parkourability parkourability, @Local(argsOnly = true) ByteBuffer startInfo, @Local(name = "fDirection") Flipping.Direction fDirection) {
            if (!original) return false;

            if (!BackflipHandler.canStart((FlippingSkill) this, parkourability, fDirection)) return true;

            startInfo.putInt(1);
            return false;
        }

        @OnlyIn(Dist.CLIENT)
        @Inject(method = "onStartInLocalClient", at = @At("TAIL"))
        private void onStartInLocalClient(Player player, Parkourability parkourability, ByteBuffer startData, CallbackInfo ci) {
            BackflipHandler.tryStart((FlippingSkill) this, player, startData);
        }

        @OnlyIn(Dist.CLIENT)
        @Inject(method = "onStartInOtherClient", at = @At("TAIL"))
        private void onStartInOtherClient(Player player, Parkourability parkourability, ByteBuffer startData, CallbackInfo ci) {
            BackflipHandler.tryStart((FlippingSkill) this, player, startData);
        }

        @Unique
        public void onStartInServer(Player player, Parkourability parkourability, ByteBuffer startData) {
            BackflipHandler.tryStart((FlippingSkill) this, player, startData);
        }

        @OnlyIn(Dist.CLIENT)
        @Unique
        public boolean wantsToShowStatusBar(LocalPlayer player, Parkourability parkourability) {
            return ((FlippingSkill) this).parcoolskill$getCooldown() > 0;
        }

        @OnlyIn(Dist.CLIENT)
        @Unique
        public float getStatusValue(LocalPlayer player, Parkourability parkourability) {
            var backflipCooldownDuration = Config.BACKFLIP_COOLDOWN_DURATION.get();
            return (float) ((FlippingSkill) this).parcoolskill$getCooldown() / backflipCooldownDuration;
        }
    }

    @Mixin(Projectile.class)
    private static class ProjectileMixin {
        @WrapOperation(method = "shootFromRotation", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getKnownMovement()Lnet/minecraft/world/phys/Vec3;"))
        private Vec3 wrapKnownMovement(Entity instance, Operation<Vec3> original) {
            if (!(instance instanceof Player player)) return original.call(instance);

            var parkourability = Parkourability.get(player);
            var flippingSkill = (FlippingSkill) parkourability.get(Flipping.class);
            if (flippingSkill.parcoolskill$getSkillTime() == 0) return original.call(instance);

            return Vec3.ZERO;
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Mixin(ActionProcessor.class)
    private static class ActionProcessorMixin {
        @WrapMethod(method = "onTick$doPreprocessInClient")
        private void wrapAnimationTick(PlayerTickEvent event, Parkourability parkourability, Operation<Void> original) {
            var player = event.getEntity();
            if (!TimeScaleHandler.clientTimer.runsTravelling(player)) return;

            original.call(event, parkourability);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Mixin(PlayerModelTransformer.class)
    private static class PlayerModelTransformerMixin {
        @Shadow
        @Final
        private Player player;

        @WrapMethod(method = "getPartialTick")
        private float wrapPartialTick(Operation<Float> original) {
            return TimeScaleHandler.isEntityEnforceableFrozen(player)
                    ? TimeScaleHandler.getScalablePartialTick(!TimeScaleHandler.isEntityOriginalFrozen(player))
                    : original.call();
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Mixin(PlayerModelRotator.class)
    private static class PlayerModelRotatorMixin {
        @Shadow
        @Final
        private Player player;

        @WrapMethod(method = "getPartialTick")
        private float wrapPartialTick(Operation<Float> original) {
            return TimeScaleHandler.isEntityEnforceableFrozen(player)
                    ? TimeScaleHandler.getScalablePartialTick(!TimeScaleHandler.isEntityOriginalFrozen(player))
                    : original.call();
        }
    }
}
