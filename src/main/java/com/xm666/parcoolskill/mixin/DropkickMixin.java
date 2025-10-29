package com.xm666.parcoolskill.mixin;

import com.alrex.parcool.common.action.impl.CatLeap;
import com.alrex.parcool.common.action.impl.Slide;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.xm666.parcoolskill.handler.DropkickHandler;
import com.xm666.parcoolskill.network.KickPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class DropkickMixin {
    @Mixin(Slide.class)
    private static class SlideMixin {
        @ModifyExpressionValue(method = "canStart", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;onGround()Z"))
        private boolean modifyOnGround(boolean original, Player player) {
            return original || Parkourability.get(player).get(CatLeap.class).isDoing();
        }

        @Definition(id = "getDashTick", method = "Lcom/alrex/parcool/common/action/impl/FastRun;getDashTick(Lcom/alrex/parcool/common/action/AdditionalProperties;)I")
        @Expression("?.getDashTick(?) > 5")
        @ModifyExpressionValue(method = "canStart", at = @At(value = "MIXINEXTRAS:EXPRESSION"))
        private boolean wrapDashCheck(boolean original, Player player) {
            return original || Parkourability.get(player).get(CatLeap.class).isDoing();
        }

        @WrapOperation(method = "onWorkingTickInLocalClient", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;scale(D)Lnet/minecraft/world/phys/Vec3;", ordinal = 1))
        private Vec3 wrapVecScale(Vec3 instance, double factor, Operation<Vec3> original, Player player) {
            return Parkourability.get(player).get(CatLeap.class).isDoing() ? instance : original.call(instance, factor);
        }

        @ModifyExpressionValue(method = "onWorkingTickInLocalClient", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;y()D"))
        private double modifyMovementY(double original, Player player) {
            return Parkourability.get(player).get(CatLeap.class).isDoing() ? Math.max(original * 0.9, original) : original;
        }
    }

    @Mixin(Minecraft.class)
    private static class MinecraftMixin {
        @Inject(method = "handleKeybinds", at = @At("TAIL"))
        private void injectHandleKeybinds(CallbackInfo ci) {
            var mc = Minecraft.getInstance();
            var player = mc.player;
            if (mc.crosshairPickEntity != null && player != null && DropkickHandler.queueAttack) {
                PacketDistributor.sendToServer(new KickPayload(mc.crosshairPickEntity.getId(), player.getId(), KickPayload.Type.DROPKICK.ordinal()));
                DropkickHandler.queueAttack = false;
            }
        }
    }
}
