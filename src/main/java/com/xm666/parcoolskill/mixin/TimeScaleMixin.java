package com.xm666.parcoolskill.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.mojang.blaze3d.vertex.PoseStack;
import com.xm666.parcoolskill.handler.TimeScaleHandler;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.server.ServerTickRateManager;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class TimeScaleMixin {
    @Mixin(TickRateManager.class)
    private static class TickRateManagerMixin {
        @ModifyReturnValue(method = "runsNormally", at = @At("RETURN"))
        private boolean modifyRunNormally(boolean original) {
            var timer = (Object) this instanceof ServerTickRateManager ? TimeScaleHandler.serverTimer : TimeScaleHandler.clientTimer;
            return original && (!TimeScaleHandler.scaleRunNormally || timer.runsTicking()) && !TimeScaleHandler.disableRunNormally;
        }
    }

    @Mixin(DeltaTracker.Timer.class)
    private static class DeltaTrackerTimerMixin {
        @ModifyReturnValue(method = "getGameTimeDeltaPartialTick", at = @At(value = "RETURN", ordinal = 1))
        private float modifyPartialTick(float original) {
            return TimeScaleHandler.scalePartialTick
                    ? Math.min(TimeScaleHandler.deltaTickRunning + original * TimeScaleHandler.clientTimer.getScale(), 1.0F)
                    : original;
        }
    }

    @Mixin(LevelRenderer.class)
    private static class LevelRendererMixin {
        @WrapOperation(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickRateManager;isEntityFrozen(Lnet/minecraft/world/entity/Entity;)Z"))
        private boolean wrapEntityFrozen(TickRateManager instance, Entity entity, Operation<Boolean> original) {
            return TimeScaleHandler.isOriginalEntityFrozen(entity);
        }

        @WrapOperation(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/DeltaTracker;getGameTimeDeltaPartialTick(Z)F", ordinal = 1))
        private float wrapEntityPartialTick(DeltaTracker instance, boolean runsNormally, Operation<Float> original, @Local Entity entity) {
            return TimeScaleHandler.isDefaultEntityFrozen(entity)
                    ? original.call(instance, runsNormally)
                    : TimeScaleHandler.getOriginalPartialTick(true);
        }

        @Inject(method = "renderEntity", at = @At("HEAD"))
        private void onRenderEntity(Entity entity, double camX, double camY, double camZ, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, CallbackInfo ci, @Share("playerPartialTick") LocalFloatRef playerPartialTickRef) {
            var playerPartialTick = TimeScaleHandler.isPlayerEntityFrozen(entity)
                    ? TimeScaleHandler.getDefaultPartialTick(!TimeScaleHandler.isOriginalEntityFrozen(entity))
                    : partialTick;
            playerPartialTickRef.set(playerPartialTick);
        }

        @ModifyArg(method = "renderEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;lerp(DDD)D"), index = 0)
        private double wrapLerpPartialTick(double delta, @Share("playerPartialTick") LocalFloatRef playerPartialTickRef) {
            return playerPartialTickRef.get();
        }
    }

    @Mixin(EntityRenderDispatcher.class)
    private static class EntityRenderDispatcherMixin {
        @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;renderShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/Entity;FFLnet/minecraft/world/level/LevelReader;F)V"), index = 4)
        private float modifyPartialTick(float partialTick, @Local(argsOnly = true) Entity entity) {
            return TimeScaleHandler.isPlayerEntityFrozen(entity)
                    ? TimeScaleHandler.getDefaultPartialTick(!TimeScaleHandler.isOriginalEntityFrozen(entity))
                    : partialTick;
        }
    }

    @Mixin(GameRenderer.class)
    private static class GameRendererMixin {
        @Shadow
        @Final
        Minecraft minecraft;

        @WrapOperation(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/DeltaTracker;getGameTimeDeltaPartialTick(Z)F"))
        private float wrapOriginalPartialTick(DeltaTracker instance, boolean runsNormally, Operation<Float> original) {
            return TimeScaleHandler.getOriginalPartialTick(runsNormally);
        }

        @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;pick(F)V"))
        private void onPick(DeltaTracker deltaTracker, CallbackInfo ci, @Local float originalPartialTick, @Share("partialTick") LocalFloatRef partialTickRef) {
            var entity = minecraft.getCameraEntity();
            var partialTick = TimeScaleHandler.isDefaultEntityFrozen(entity)
                    ? TimeScaleHandler.getDefaultPartialTick(!TimeScaleHandler.isOriginalEntityFrozen(entity))
                    : originalPartialTick;
            partialTickRef.set(partialTick);
        }

        @ModifyArg(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;pick(F)V"))
        private float wrapPickPartialTick(float original, @Share("partialTick") LocalFloatRef partialTickRef) {
            return partialTickRef.get();
        }

        @ModifyArg(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setup(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;ZZF)V"))
        private float modifySetupPartialTick(float original, @Share("partialTick") LocalFloatRef partialTickRef) {
            return partialTickRef.get();
        }

        @ModifyArg(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;renderItemInHand(Lnet/minecraft/client/Camera;FLorg/joml/Matrix4f;)V"))
        private float modifyRenderPartialTick(float original, @Share("partialTick") LocalFloatRef partialTickRef) {
            return partialTickRef.get();
        }
    }

    @Mixin(Camera.class)
    private static class CameraMixin {
        @Inject(method = "setup", at = @At("HEAD"))
        private void onSetup(BlockGetter level, Entity entity, boolean detached, boolean thirdPersonReverse, float partialTick, CallbackInfo ci, @Share("playerPartialTick") LocalFloatRef playerPartialTickRef) {
            var playerPartialTick = TimeScaleHandler.isPlayerEntityFrozen(entity)
                    ? TimeScaleHandler.getDefaultPartialTick(!TimeScaleHandler.isOriginalEntityFrozen(entity))
                    : partialTick;
            playerPartialTickRef.set(playerPartialTick);
        }

        @ModifyArg(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;lerp(DDD)D"), index = 0)
        private double modifyPartialTick(double delta, @Share("playerPartialTick") LocalFloatRef playerPartialTickRef) {
            return playerPartialTickRef.get();
        }
    }

    @Mixin(Minecraft.class)
    private static class MinecraftMixin {
        @WrapOperation(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;isLevelRunningNormally()Z"))
        private boolean wrapRunNormally(Minecraft instance, Operation<Boolean> original) {
            TimeScaleHandler.scaleRunNormally = false;
            var normally = original.call(instance);
            TimeScaleHandler.scaleRunNormally = true;

            return normally;
        }
    }

    @Mixin(LivingEntity.class)
    private static class LivingEntityMixin {
        @WrapWithCondition(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;travel(Lnet/minecraft/world/phys/Vec3;)V"))
        private boolean wrapTravel(LivingEntity instance, Vec3 travelVector) {
            var timer = instance.level().isClientSide ? TimeScaleHandler.clientTimer : TimeScaleHandler.serverTimer;
            return timer.runsTraveling(instance);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Mixin(Entity.class)
    private static class EntityMixin {
        @Inject(method = "setOldPosAndRot", at = @At("HEAD"))
        private void onSetOldPos(CallbackInfo ci, @Share("setOldPos") LocalBooleanRef setOldPosRef) {
            var setOldPos = TimeScaleHandler.clientTimer.runsTraveling((Entity) (Object) this);
            setOldPosRef.set(setOldPos);
        }

        @WrapWithCondition(method = "setOldPosAndRot", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/Entity;xo:D", opcode = Opcodes.PUTFIELD))
        private boolean wrapXo(Entity instance, double value, @Share("setOldPos") LocalBooleanRef setOldPosRef) {
            return setOldPosRef.get();
        }

        @WrapWithCondition(method = "setOldPosAndRot", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/Entity;yo:D", opcode = Opcodes.PUTFIELD))
        private boolean wrapYo(Entity instance, double value, @Share("setOldPos") LocalBooleanRef setOldPosRef) {
            return setOldPosRef.get();
        }

        @WrapWithCondition(method = "setOldPosAndRot", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/Entity;zo:D", opcode = Opcodes.PUTFIELD))
        private boolean wrapZo(Entity instance, double value, @Share("setOldPos") LocalBooleanRef setOldPosRef) {
            return setOldPosRef.get();
        }

        @WrapWithCondition(method = "setOldPosAndRot", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/Entity;xOld:D", opcode = Opcodes.PUTFIELD))
        private boolean wrapXOld(Entity instance, double value, @Share("setOldPos") LocalBooleanRef setOldPosRef) {
            return setOldPosRef.get();
        }

        @WrapWithCondition(method = "setOldPosAndRot", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/Entity;yOld:D", opcode = Opcodes.PUTFIELD))
        private boolean wrapYOld(Entity instance, double value, @Share("setOldPos") LocalBooleanRef setOldPosRef) {
            return setOldPosRef.get();
        }

        @WrapWithCondition(method = "setOldPosAndRot", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/Entity;zOld:D", opcode = Opcodes.PUTFIELD))
        private boolean wrapZOld(Entity instance, double value, @Share("setOldPos") LocalBooleanRef setOldPosRef) {
            return setOldPosRef.get();
        }

        @WrapWithCondition(method = "baseTick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/Entity;walkDistO:F", opcode = Opcodes.PUTFIELD))
        private boolean wrapWalkDistO(Entity instance, float value) {
            return TimeScaleHandler.clientTimer.runsTraveling((Entity) (Object) this);
        }
    }

    @Mixin(LivingEntityRenderer.class)
    private static class LivingEntityRendererMixin {
        @ModifyArg(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/WalkAnimationState;speed(F)F"))
        private float modifySpeedPartialTick(float partialTick, @Local(argsOnly = true) LivingEntity entity) {
            return TimeScaleHandler.isPlayerEntityFrozen(entity)
                    ? TimeScaleHandler.getDefaultPartialTick(!TimeScaleHandler.isOriginalEntityFrozen(entity))
                    : partialTick;
        }

        @ModifyArg(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/WalkAnimationState;position(F)F"))
        private float modifyPositionPartialTick(float partialTick, @Local(argsOnly = true) LivingEntity entity) {
            return TimeScaleHandler.isPlayerEntityFrozen(entity)
                    ? TimeScaleHandler.getDefaultPartialTick(!TimeScaleHandler.isOriginalEntityFrozen(entity))
                    : partialTick;
        }
    }
}
