package com.xm666.parcoolskill.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.xm666.parcoolskill.handler.TimeScaleHandler;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

public class TimeScaleMixin {
    @Mixin(TickRateManager.class)
    private static class TickRateManagerMixin {
        @ModifyReturnValue(method = "runsNormally", at = @At("RETURN"))
        private boolean modifyRunsNormally(boolean original) {
            return original && (!TimeScaleHandler.modifyRunsNormally || TimeScaleHandler.timer.runsTicking()) && TimeScaleHandler.enableRunsNormally;
        }
    }

    @Mixin(DeltaTracker.Timer.class)
    private static class DeltaTrackerTimerMixin {
        @ModifyReturnValue(method = "getGameTimeDeltaPartialTick", at = @At(value = "RETURN", ordinal = 1))
        private float modifyGameTimeDeltaPartialTick(float original) {
            return TimeScaleHandler.modifyGameTimeDeltaPartialTick
                    ? Math.min(TimeScaleHandler.deltaTickRunning + original * TimeScaleHandler.timer.getScale(), 1.0F)
                    : original;
        }
    }

    @Mixin(LevelRenderer.class)
    private static class LevelRendererMixin {
        @WrapOperation(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickRateManager;isEntityFrozen(Lnet/minecraft/world/entity/Entity;)Z"))
        private boolean wrapEntityFrozen(TickRateManager instance, Entity entity, Operation<Boolean> original) {
            TimeScaleHandler.modifyRunsNormally = false;
            var frozen = instance.isEntityFrozen(entity);
            TimeScaleHandler.modifyRunsNormally = true;

            return frozen;
        }

        @WrapOperation(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/DeltaTracker;getGameTimeDeltaPartialTick(Z)F", ordinal = 1))
        private float wrapGameTimeDeltaPartialTick(DeltaTracker instance, boolean b, Operation<Float> original, @Local TickRateManager tickratemanager, @Local Entity entity) {
            TimeScaleHandler.enableRunsNormally = false;
            var frozen = tickratemanager.isEntityFrozen(entity);
            TimeScaleHandler.enableRunsNormally = true;

            TimeScaleHandler.modifyGameTimeDeltaPartialTick = frozen;
            var partialTick = original.call(instance, b);
            TimeScaleHandler.modifyGameTimeDeltaPartialTick = true;

            return partialTick;
        }
    }

    @Mixin(GameRenderer.class)
    private static class GameRendererMixin {
        @Shadow
        @Final
        Minecraft minecraft;

        @WrapOperation(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickRateManager;isEntityFrozen(Lnet/minecraft/world/entity/Entity;)Z"))
        private boolean wrapEntityFrozen(TickRateManager instance, Entity entity, Operation<Boolean> original) {
            TimeScaleHandler.modifyRunsNormally = false;
            var frozen = instance.isEntityFrozen(entity);
            TimeScaleHandler.modifyRunsNormally = true;

            return frozen;
        }

        @ModifyVariable(method = "renderLevel", at = @At(value = "LOAD", ordinal = 2), ordinal = 0)
        private float modifyPartialTick(float original, DeltaTracker deltaTracker, @Local Entity entity) {
            var tickRateManager = minecraft.level.tickRateManager();

            TimeScaleHandler.enableRunsNormally = false;
            var frozen = tickRateManager.isEntityFrozen(entity);
            TimeScaleHandler.enableRunsNormally = true;

            if (frozen) return original;

            TimeScaleHandler.modifyGameTimeDeltaPartialTick = false;
            var partialTick = deltaTracker.getGameTimeDeltaPartialTick(true);
            TimeScaleHandler.modifyGameTimeDeltaPartialTick = true;

            return partialTick;
        }
    }

    @Mixin(Minecraft.class)
    private static class MinecraftMixin {
        @WrapOperation(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;isLevelRunningNormally()Z"))
        private boolean wrapLevelRunningNormally(Minecraft instance, Operation<Boolean> original) {
            TimeScaleHandler.modifyRunsNormally = false;
            var normally = original.call(instance);
            TimeScaleHandler.modifyRunsNormally = true;

            return normally;
        }
    }
}
