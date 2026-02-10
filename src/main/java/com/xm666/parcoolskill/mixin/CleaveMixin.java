package com.xm666.parcoolskill.mixin;

import com.alrex.parcool.common.action.impl.ChargeJump;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.xm666.parcoolskill.handler.CleaveHandler;
import com.xm666.parcoolskill.handler.SkillAttackHandler;
import com.xm666.parcoolskill.network.SkillAttackPayload;
import com.xm666.parcoolskill.skill.JumpSkill;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.common.ItemAbilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.stream.Stream;

public class CleaveMixin {
    @Mixin(Minecraft.class)
    private static class MinecraftMixin {
        @Inject(method = "handleKeybinds", at = @At("HEAD"))
        private void onHandleKeybinds(CallbackInfo ci) {
            var mc = Minecraft.getInstance();
            var player = mc.player;
            if (player == null || !player.getWeaponItem().canPerformAction(ItemAbilities.SWORD_SWEEP)) return;

            var skillJump = (JumpSkill) Parkourability.get(player).get(ChargeJump.class);
            if (skillJump.parcoolskill$getAttackTime() == 0) return;

            var interactionRange = player.entityInteractionRange() * 2.0;
            var level = player.level();
            var sweepingEdge = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.SWEEPING_EDGE);
            var hitLimit = player.getWeaponItem().getEnchantmentLevel(sweepingEdge) + 1;

            var eyePosition = player.getEyePosition();
            var viewVector = player.getViewVector(1.0F);
            var interactionVector = viewVector.scale(interactionRange);
            var interactionPosition = eyePosition.add(interactionVector);
            var aabb = player.getBoundingBox().expandTowards(interactionVector).inflate(1.0);
            var entityHits = Stream.concat(
                            Arrays.stream(CleaveHandler.getEntityHits(
                                    player,
                                    eyePosition,
                                    interactionPosition,
                                    aabb,
                                    (entity) -> !entity.isSpectator() && entity.isPickable(),
                                    0.0F,
                                    hitLimit
                            )), Arrays.stream(CleaveHandler.getEntityHits(
                                    player,
                                    eyePosition,
                                    interactionPosition,
                                    aabb,
                                    (entity) -> !entity.isSpectator() && entity.isPickable(),
                                    0.3F,
                                    hitLimit
                            ))).filter(CleaveHandler.entityHits::add)
                    .toArray(Entity[]::new);
            for (var entityHit : entityHits) {
                SkillAttackHandler.attack(entityHit, player, SkillAttackPayload.SkillAttackType.CLEAVE);
            }
        }
    }
}
