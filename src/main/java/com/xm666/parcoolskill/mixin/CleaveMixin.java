package com.xm666.parcoolskill.mixin;

import com.alrex.parcool.common.action.impl.ChargeJump;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.xm666.parcoolskill.handler.CleaveHandler;
import com.xm666.parcoolskill.network.SkillAttackPayload;
import com.xm666.parcoolskill.skill.JumpSkill;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

public class CleaveMixin {
    @Mixin(Minecraft.class)
    private static class MinecraftMixin {
        @Inject(method = "handleKeybinds", at = @At("HEAD"))
        private void onHandleKeybinds(CallbackInfo ci) {
            var mc = Minecraft.getInstance();
            var player = mc.player;
            if (player == null) return;

            var skillJump = (JumpSkill) Parkourability.get(player).get(ChargeJump.class);
            if (skillJump.parcoolskill$getAttackTime() == 0) return;

            var entityInteractionRange = player.entityInteractionRange() * 2.0;
            var eyePosition = player.getEyePosition();
            var viewVector = player.getViewVector(1.0F);
            var endPosition = eyePosition.add(viewVector.x * entityInteractionRange, viewVector.y * entityInteractionRange, viewVector.z * entityInteractionRange);
            var aabb = player.getBoundingBox().expandTowards(viewVector.scale(entityInteractionRange)).inflate(1.0, 1.0, 1.0);
            var entitiesHit = CleaveHandler.getEntitiesHit(player, eyePosition, endPosition, aabb, (entity) -> !entity.isSpectator() && entity.isPickable(), 1.0F);
            for (var entity : Arrays.stream(entitiesHit).filter(CleaveHandler.entitiesHit::add).toArray(Entity[]::new)) {
                PacketDistributor.sendToServer(new SkillAttackPayload(entity.getId(), player.getId(), SkillAttackPayload.SkillAttackType.CLEAVE.ordinal()));
            }
        }
    }
}
