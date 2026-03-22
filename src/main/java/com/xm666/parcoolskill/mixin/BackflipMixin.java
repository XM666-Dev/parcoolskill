package com.xm666.parcoolskill.mixin;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.ActionProcessor;
import com.alrex.parcool.common.action.impl.Flipping;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.xm666.parcoolskill.Config;
import com.xm666.parcoolskill.handler.BackflipHandler;
import com.xm666.parcoolskill.handler.SkillParticleHandler;
import com.xm666.parcoolskill.handler.TimeScaleHandler;
import com.xm666.parcoolskill.network.SkillParticlePayload;
import com.xm666.parcoolskill.skill.FlipSkill;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.ByteBuffer;

public class BackflipMixin {
    @Mixin(Flipping.class)
    private static class FlippingMixin {
        @ModifyExpressionValue(method = "canStart", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isShiftKeyDown()Z"))
        private boolean modifyShiftKeyDown(boolean original, @Local(name = "fDirection") Flipping.Direction fDirection) {
            return original && fDirection != Flipping.Direction.Back;
        }

        @Inject(method = "canStart", at = @At("TAIL"))
        private void onStart(Player player, Parkourability parkourability, ByteBuffer startInfo, CallbackInfoReturnable<Boolean> cir) {
            startInfo.putInt(player.isShiftKeyDown() && ((FlipSkill) this).parcoolskill$getCooldown() == 0 ? 1 : 0);
        }

        @Inject(method = "onStartInLocalClient", at = @At("TAIL"))
        private void onStartInLocal(Player player, Parkourability parkourability, ByteBuffer startInfo, CallbackInfo ci) {
            if (!BackflipHandler.canStart(startInfo)) return;

            BackflipHandler.onStart(player, (FlipSkill) this);

            if (!ParCoolConfig.Client.Booleans.EnableActionSounds.get()) return;

            player.playSound(SoundEvents.CHARGE_JUMP.get(), 1.0F, 1.0F);
        }

        @Inject(method = "onStartInOtherClient", at = @At("TAIL"))
        private void onStartInOther(Player player, Parkourability parkourability, ByteBuffer startInfo, CallbackInfo ci) {
            if (!BackflipHandler.canStart(startInfo) || !ParCoolConfig.Client.Booleans.EnableActionSounds.get())
                return;

            player.playSound(SoundEvents.CHARGE_JUMP.get(), 1.0F, 1.0F);
        }

        @SuppressWarnings("AddedMixinMembersNamePattern")
        @Unique
        public void onStartInServer(Player player, Parkourability parkourability, ByteBuffer startInfo) {
            if (!BackflipHandler.canStart(startInfo)) return;

            BackflipHandler.onStart(player, (FlipSkill) this);

            var backflipBulletTimeScale = Config.BACKFLIP_BULLET_TIME_SCALE.get().floatValue();
            var backflipBulletTimeDuration = Config.BACKFLIP_BULLET_TIME_DURATION.get();
            TimeScaleHandler.applyScale(player, backflipBulletTimeScale, backflipBulletTimeDuration, 40);

            SkillParticleHandler.emit(SkillParticlePayload.Type.SILENT_EFFECT, player);
        }
    }

    @Mixin(Projectile.class)
    private static class ProjectileMixin {
        @WrapOperation(method = "shootFromRotation", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getKnownMovement()Lnet/minecraft/world/phys/Vec3;"))
        private Vec3 wrapKnownMovement(Entity instance, Operation<Vec3> original) {
            if (!(instance instanceof Player player)) return original.call(instance);

            var flipSkill = (FlipSkill) Parkourability.get(player).get(Flipping.class);
            if (flipSkill.parcoolskill$getSkillTime() == 0) return original.call(instance);

            return Vec3.ZERO;
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Mixin(ActionProcessor.class)
    private static class ActionProcessorMixin {
        @WrapMethod(method = "onTick$doPreprocessInClient")
        private void wrapAnimationTick(PlayerTickEvent event, Parkourability parkourability, Operation<Void> original) {
            var player = event.getEntity();
            if (!TimeScaleHandler.clientTimer.runsTraveling(player)) return;

            original.call(event, parkourability);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Mixin(PlayerModelTransformer.class)
    private static class PlayerModelTransformerMixin {
        @Shadow
        @Final
        private Player player;

        @ModifyReturnValue(method = "getPartialTick", at = @At("RETURN"))
        public float modifyPartialTick(float original) {
            return TimeScaleHandler.isPlayerEntityFrozen(player)
                    ? TimeScaleHandler.getDefaultPartialTick(!TimeScaleHandler.isOriginalEntityFrozen(player))
                    : original;
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Mixin(PlayerModelRotator.class)
    private static class PlayerModelRotatorMixin {
        @Shadow
        @Final
        private Player player;

        @ModifyReturnValue(method = "getPartialTick", at = @At("RETURN"))
        public float modifyPartialTick(float original) {
            return TimeScaleHandler.isPlayerEntityFrozen(player)
                    ? TimeScaleHandler.getDefaultPartialTick(!TimeScaleHandler.isOriginalEntityFrozen(player))
                    : original;
        }
    }
}
