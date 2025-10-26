package com.xm666.parcoolskill.mixin;

import com.xm666.parcoolskill.handler.SlidekickHandler;
import com.xm666.parcoolskill.network.KickPayload;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class SlidekickMixin {
    @Mixin(Minecraft.class)
    private static class MinecraftMixin {
        @Inject(method = "handleKeybinds", at = @At("TAIL"))
        private void injectHandleKeybinds(CallbackInfo ci) {
            var mc = Minecraft.getInstance();
            var player = mc.player;
            if (mc.crosshairPickEntity != null && player != null && SlidekickHandler.queueAttack) {
                PacketDistributor.sendToServer(new KickPayload(mc.crosshairPickEntity.getId(), player.getId(), KickPayload.Type.SLIDEKICK.ordinal()));
                SlidekickHandler.queueAttack = false;
            }
        }
    }
}
