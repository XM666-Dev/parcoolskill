package com.xm666.parcoolskill.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.xm666.parcoolskill.handler.BulletTimeHandler;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.server.ServerTickRateManager;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

public class BulletTimeMixin {
    @Mixin(TickRateManager.class)
    private static class TickRateManagerMixin {
        @ModifyReturnValue(method = "runsNormally", at = @At("RETURN"))
        private boolean modifyRunsNormally(boolean original) {
            return original && (!BulletTimeHandler.modifyRunsNormally || ((Object) this instanceof ServerTickRateManager ? BulletTimeHandler.serverTicker : BulletTimeHandler.clientTicker).runTick) && BulletTimeHandler.canRunsNormally;
        }
    }

    @Mixin(DeltaTracker.Timer.class)
    private static class DeltaTrackerTimerMixin {
        @ModifyReturnValue(method = "getGameTimeDeltaPartialTick", at = @At("RETURN"))
        private float modifyGameTimeDeltaPartialTick(float original) {
            return BulletTimeHandler.modifyGameTimeDeltaPartialTick ? original * BulletTimeHandler.clientTicker.getTimeScale() + BulletTimeHandler.clientTicker.partialTick : original;
        }
    }

    @Mixin(LevelRenderer.class)
    private static class LevelRendererMixin {
        @WrapOperation(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/DeltaTracker;getGameTimeDeltaPartialTick(Z)F"))
        private float wrapGameTimeDeltaPartialTick(DeltaTracker instance, boolean b, Operation<Float> original, @Local TickRateManager tickratemanager, @Local Entity entity) {
            BulletTimeHandler.canRunsNormally = false;
            var frozen = tickratemanager.isEntityFrozen(entity);
            BulletTimeHandler.canRunsNormally = true;
            BulletTimeHandler.modifyGameTimeDeltaPartialTick = frozen;
            var partialTick = original.call(instance, frozen);
            BulletTimeHandler.modifyGameTimeDeltaPartialTick = true;
            return partialTick;
        }
    }

    @Mixin(GameRenderer.class)
    private static class GameRendererMixin {
        @Final
        @Shadow
        Minecraft minecraft;

        @ModifyArg(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setup(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;ZZF)V"), index = 4)
        private float modifyPartialTick(float partialTick, @Local Entity entity, @Local(argsOnly = true) DeltaTracker deltaTracker) {
            BulletTimeHandler.canRunsNormally = false;
            var frozen = this.minecraft.level != null && this.minecraft.level.tickRateManager().isEntityFrozen(entity);
            BulletTimeHandler.canRunsNormally = true;
            BulletTimeHandler.modifyGameTimeDeltaPartialTick = frozen;
            partialTick = deltaTracker.getGameTimeDeltaPartialTick(true);
            BulletTimeHandler.modifyGameTimeDeltaPartialTick = true;
            return partialTick;
        }
    }

    @Mixin(Minecraft.class)
    private static class MinecraftMixin {
        @WrapOperation(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;isLevelRunningNormally()Z"))
        private boolean wrapLevelRunningNormally(Minecraft instance, Operation<Boolean> original) {
            BulletTimeHandler.modifyRunsNormally = false;
            var levelRunningNormally = original.call(instance);
            BulletTimeHandler.modifyRunsNormally = true;
            return levelRunningNormally;
        }
    }
}
