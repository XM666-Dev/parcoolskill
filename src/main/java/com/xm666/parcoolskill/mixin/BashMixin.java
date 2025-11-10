package com.xm666.parcoolskill.mixin;

import com.alrex.parcool.common.action.impl.ChargeJump;
import com.xm666.parcoolskill.action.BashJump;
import net.minecraft.client.player.Input;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

public class BashMixin {
    @Mixin(ChargeJump.class)
    private static class ChargeJumpMixin implements BashJump {
        @Unique
        private boolean parcoolskill$queueAttack;

        @Redirect(method = "onClientTick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/player/Input;up:Z"))
        private boolean redirectInputUp(Input instance) {
            return false;
        }

        @Redirect(method = "onClientTick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/player/Input;down:Z"))
        private boolean redirectInputDown(Input instance) {
            return false;
        }

        @Redirect(method = "onClientTick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/player/Input;right:Z"))
        private boolean redirectInputRight(Input instance) {
            return false;
        }

        @Redirect(method = "onClientTick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/player/Input;left:Z"))
        private boolean redirectInputLeft(Input instance) {
            return false;
        }

        @Override
        public boolean parcoolskill$isQueueAttack() {
            return parcoolskill$queueAttack;
        }

        @Override
        public void parcoolskill$setQueueAttack(boolean queue) {
            parcoolskill$queueAttack = queue;
        }
    }
}
