package com.xm666.parcoolskill.mixin;

import com.alrex.parcool.common.action.impl.ChargeJump;
import net.minecraft.client.player.Input;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

public class BashMixin {
    @Mixin(ChargeJump.class)
    private static class ChargeJumpMixin {
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
    }
}
