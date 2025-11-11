package com.xm666.parcoolskill.mixin;

import com.alrex.parcool.common.action.impl.ChargeJump;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.llamalad7.mixinextras.sugar.Local;
import com.xm666.parcoolskill.action.SkillJump;
import com.xm666.parcoolskill.handler.BashHandler;
import com.xm666.parcoolskill.handler.StaminaHandler;
import com.xm666.parcoolskill.network.StopChargePayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.stream.Collectors;

public class BashMixin {
    @Mixin(ChargeJump.class)
    private static class ChargeJumpMixin implements SkillJump {
        @Shadow
        private int chargeTick;
        @Shadow
        private int notChargeTick;

        @Override
        public int parcoolskill$getChargeTick() {
            return chargeTick;
        }

        @Override
        public int parcoolskill$getNotChargeTick() {
            return notChargeTick;
        }
    }

    @Mixin(Player.class)
    private static class PlayerMixin {
        @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
        private void injectAttack(Entity target, CallbackInfo ci, @Local(ordinal = 0) boolean fullStrength) {
            if (fullStrength) {
                if (!((Object) this instanceof ServerPlayer source)) return;
                var jump = (SkillJump) Parkourability.get(source).get(ChargeJump.class);
                if (jump.parcoolskill$getChargeTick() < ChargeJump.JUMP_ANIMATION_TICK) return;
                var weapon = source.getWeaponItem();
                if (weapon == null || !weapon.getTags().map(TagKey::location).map(ResourceLocation::toString).collect(Collectors.toSet()).contains("minecraft:axes"))
                    return;
                if (!StaminaHandler.consumeStamina(source, 200)) return;
                BashHandler.queueAttack = true;
                PacketDistributor.sendToPlayer(source, StopChargePayload.INSTANCE);
            }
        }
    }
}
