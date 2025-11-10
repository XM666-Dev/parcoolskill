package com.xm666.parcoolskill.mixin;

import com.alrex.parcool.common.action.impl.Slide;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.xm666.parcoolskill.action.SlidekickSlide;
import com.xm666.parcoolskill.network.KickPayload;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class SlidekickMixin {
    @Mixin(Slide.class)
    private static class SlideMixin implements SlidekickSlide {
        @Unique
        private boolean parcoolskill$queueSlidekick;
        @Unique
        private boolean parcoolskill$queueInvulnerable;

        @Override
        public boolean parcoolskill$isQueueSlidekick() {
            return parcoolskill$queueSlidekick;
        }

        @Override
        public void parcoolskill$setQueueSlidekick(boolean queue) {
            parcoolskill$queueSlidekick = queue;
        }

        @Override
        public boolean parcoolskill$isQueueInvulnerable() {
            return parcoolskill$queueInvulnerable;
        }

        @Override
        public void parcoolskill$setQueueInvulnerable(boolean queue) {
            parcoolskill$queueInvulnerable = queue;
        }
    }

    @Mixin(Minecraft.class)
    private static class MinecraftMixin {
        @Inject(method = "handleKeybinds", at = @At("TAIL"))
        private void injectHandleKeybinds(CallbackInfo ci) {
            var mc = Minecraft.getInstance();
            var player = mc.player;
            if (mc.crosshairPickEntity != null && player != null) {
                var slide = (SlidekickSlide) Parkourability.get(player).get(Slide.class);
                if (!slide.parcoolskill$isQueueSlidekick()) return;
                PacketDistributor.sendToServer(new KickPayload(mc.crosshairPickEntity.getId(), player.getId(), KickPayload.Type.SLIDEKICK.ordinal()));
                slide.parcoolskill$setQueueSlidekick(false);
            }
        }
    }
}
