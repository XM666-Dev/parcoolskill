package com.xm666.parcoolskill.handler;

import com.xm666.parcoolskill.network.SkillAttackPayload;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SkillAttackHandler {
    public static void handlePayload(final SkillAttackPayload payload, final IPayloadContext context) {
        var level = context.player().level();
        var targetEntity = level.getEntity(payload.targetEntityId());
        var sourceEntity = level.getEntity(payload.sourceEntityId());
        var skillAttackType = SkillAttackPayload.SkillAttackType.values()[payload.skillAttackType()];
        if (!(targetEntity instanceof LivingEntity target) || !(sourceEntity instanceof Player player)) return;

        if (skillAttackType == SkillAttackPayload.SkillAttackType.DROPKICK || skillAttackType == SkillAttackPayload.SkillAttackType.HEEL_HOOK) {
            SlideSkillHandler.handleAttack(target, player, level, skillAttackType);
        } else if (skillAttackType == SkillAttackPayload.SkillAttackType.CLEAVE) {
            CleaveHandler.handleAttack(target, player);
        }
    }
}
