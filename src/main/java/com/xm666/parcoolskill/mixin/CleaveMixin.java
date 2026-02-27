package com.xm666.parcoolskill.mixin;

import com.alrex.parcool.common.action.impl.ChargeJump;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.xm666.parcoolskill.Config;
import com.xm666.parcoolskill.handler.CleaveHandler;
import com.xm666.parcoolskill.handler.SkillHandler;
import com.xm666.parcoolskill.network.SkillPayload;
import com.xm666.parcoolskill.skill.JumpSkill;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.Enchantments;
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
            if (player == null || !CleaveHandler.isReadyForAttack(player)) return;

            var cleaveHitRangeMultiplier = Config.CLEAVE_HIT_RANGE_MULTIPLIER.get();
            var cleaveHitRadius = Config.CLEAVE_HIT_RADIUS.get();
            var cleaveHitLimitBase = Config.CLEAVE_HIT_LIMIT_BASE.get();
            var jumpSkill = (JumpSkill) Parkourability.get(player).get(ChargeJump.class);
            var sweepingEdge = player.level().registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.SWEEPING_EDGE);
            var range = SkillHandler.getEntityHitRange(player, player.entityInteractionRange() * cleaveHitRangeMultiplier);
            var limit = cleaveHitLimitBase + player.getWeaponItem().getEnchantmentLevel(sweepingEdge);
            var targets = Stream.concat(
                    Arrays.stream(SkillHandler.getEntityHits(player, range, 0.0, limit)),
                    Arrays.stream(SkillHandler.getEntityHits(player, range, cleaveHitRadius, 1))
            ).filter(jumpSkill::parcoolskill$addEntityHit).toArray(Entity[]::new);
            for (var target : targets) {
                SkillHandler.use(SkillPayload.Type.CLEAVE_ATTACK, player, target);
                player.resetAttackStrengthTicker();
            }
        }
    }
}
