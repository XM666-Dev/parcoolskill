package com.xm666.parcoolskill.mixin.parcoolskill.event;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.xm666.parcoolskill.event.SweepAttackEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class SweepAttackMixin {
    @Mixin(Player.class)
    private static class PlayerMixin {
        @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
        private void onHurt(Entity instance, CallbackInfo ci, @Local(name = "flag2") LocalBooleanRef doSweep) {
            var player = (Player) (Object) this;
            var sweepEvent = new SweepAttackEvent(player, instance, doSweep.get());
            MinecraftForge.EVENT_BUS.post(sweepEvent);
            doSweep.set(sweepEvent.isSweeping());
        }
    }
}
