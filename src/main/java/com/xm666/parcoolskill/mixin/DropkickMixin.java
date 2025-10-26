package com.xm666.parcoolskill.mixin;

import com.alrex.parcool.common.action.impl.CatLeap;
import com.alrex.parcool.common.action.impl.Slide;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.xm666.parcoolskill.handler.DropkickHandler;
import com.xm666.parcoolskill.network.DropkickPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.ByteBuffer;

public class DropkickMixin {
    @Mixin(Slide.class)
    private static class SlideMixin {
        @WrapOperation(method = "canStart", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;onGround()Z"))
        private boolean modifyOnGround(Player player, Operation<Boolean> original) {
            var parkourability = Parkourability.get(player);
            //ParCoolSkill.LOGGER.info("{},{},{},{},{},{},{}", KeyRecorder.keyCrawlState.isPressed(), player.onGround(), !parkourability.get(Roll.class).isDoing(), !parkourability.get(Tap.class).isDoing(), parkourability.get(Crawl.class).isDoing(), !player.isInWaterOrBubble(), parkourability.get(FastRun.class).getDashTick(parkourability.getAdditionalProperties()) > 5);
            return parkourability.get(CatLeap.class).isDoing() || original.call(player);
        }

        @Definition(id = "getDashTick", method = "Lcom/alrex/parcool/common/action/impl/FastRun;getDashTick(Lcom/alrex/parcool/common/action/AdditionalProperties;)I")
        @Expression("?.getDashTick(?) > 5")
        @WrapOperation(method = "canStart", at = @At(value = "MIXINEXTRAS:EXPRESSION"))
        private boolean wrapDashCheck(int left, int right, Operation<Boolean> original, Player player) {
            var parkourability = Parkourability.get(player);
            return parkourability.get(CatLeap.class).isDoing() || original.call(left, right);
        }

        public void onStart(Player player, Parkourability parkourability, ByteBuffer startData) {
            if (parkourability.get(CatLeap.class).isDoing()) {
                DropkickHandler.queueAttack = true;
                DropkickHandler.queueInvulnerable = true;
            }
        }

        @Inject(method = "onStop", at = @At("TAIL"))
        private void injectOnStop(Player player, CallbackInfo ci) {
            DropkickHandler.queueAttack = false;
        }
    }

    @Mixin(Minecraft.class)
    private static class MinecraftMixin {
        @Inject(method = "handleKeybinds", at = @At("TAIL"))
        private void injectHandleKeybinds(CallbackInfo ci) {
            var mc = Minecraft.getInstance();
            var player = mc.player;
            if (mc.crosshairPickEntity != null && player != null && DropkickHandler.queueAttack) {
                PacketDistributor.sendToServer(new DropkickPayload(mc.crosshairPickEntity.getId(), player.getId()));
                DropkickHandler.queueAttack = false;
            }
        }
    }
}
