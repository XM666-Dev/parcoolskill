package com.xm666.parcoolskill.mixin;

import com.alrex.parcool.common.action.BehaviorEnforcer;
import com.alrex.parcool.common.action.impl.Dodge;
import com.alrex.parcool.config.ParCoolConfig;
import com.xm666.parcoolskill.handler.FlickFlackHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class FlickFlackMixin {
    @Mixin(Dodge.class)
    private static class DodgeMixin {
        @ModifyArg(method = "onStartInLocalClient", at = @At(value = "INVOKE", target = "Lcom/alrex/parcool/common/action/BehaviorEnforcer;addMarkerCancellingJump(Lcom/alrex/parcool/common/action/BehaviorEnforcer$ID;Lcom/alrex/parcool/common/action/BehaviorEnforcer$Marker;)V"), index = 1)
        private BehaviorEnforcer.Marker modifyJumpCancelMarker(BehaviorEnforcer.Marker marker) {
            var control = ParCoolConfig.Client.getInstance().FlipControl.get();
            return () -> marker.remain() && !control.isInputDone(false);
        }
    }

    @Mixin(Minecraft.class)
    private static class MinecraftMixin {
        @Inject(method = "handleKeybinds", at = @At("HEAD"))
        private void onHandleKeybinds(CallbackInfo ci) {
            var mc = Minecraft.getInstance();
            if (!(mc.hitResult instanceof EntityHitResult)) return;

            var player = mc.player;
            if (player == null || !FlickFlackHandler.isReadyForVault(player)) return;

            FlickFlackHandler.onFlippingVault(player);
        }
    }
}
