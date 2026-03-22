package com.xm666.parcoolskill.mixin;

import com.xm666.parcoolskill.handler.FlickFlackHandler;
import com.xm666.parcoolskill.handler.SkillHandler;
import com.xm666.parcoolskill.network.SkillPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class FlickFlackMixin {
    @Mixin(Minecraft.class)
    private static class MinecraftMixin {
        @Inject(method = "handleKeybinds", at = @At("HEAD"))
        private void onHandleKeybinds(CallbackInfo ci) {
            var mc = Minecraft.getInstance();
            if (!(mc.hitResult instanceof EntityHitResult entityHitResult)) return;

            var player = mc.player;
            if (player == null || !FlickFlackHandler.isReadyForAttack(player)) return;

            var target = entityHitResult.getEntity();
            SkillHandler.use(SkillPayload.Type.FLICK_FLACK, player, target);
            player.resetAttackStrengthTicker();

            var movement = player.getDeltaMovement();
            var lookAngle = player.getLookAngle();
            var lookAngleVector = new Vec2((float) lookAngle.x, (float) lookAngle.z);
            var speed = player.zza >= 0 ? 0.75F : -0.75F;
            var lookAngleDirection = lookAngleVector.normalized().scale(speed);
            player.setDeltaMovement(lookAngleDirection.x, movement.y + 0.2, lookAngleDirection.y);
        }
    }
}
