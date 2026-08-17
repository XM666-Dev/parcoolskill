package com.xm666.parcoolskill.client;

import com.alrex.parcool.common.action.impl.ChargeJump;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.xm666.parcoolskill.Config;
import com.xm666.parcoolskill.handler.SkillHandler;
import com.xm666.parcoolskill.network.SkillPayload;
import com.xm666.parcoolskill.skill.JumpSkill;
import com.xm666.parcoolskill.skill.handler.CleaveHandler;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.InputEvent;

public class CleaveClientHandler {
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onClickInput(InputEvent.InteractionKeyMappingTriggered event) {
        if (event.isPickBlock()) return;

        var mc = Minecraft.getInstance();
        var player = mc.player;
        if (player == null) return;

        if (CleaveHandler.isAttacking(player)) {
            event.setCanceled(true);
            event.setSwingHand(false);
            return;
        }

        if (!event.isAttack() || !CleaveHandler.isCharging(player)) return;

        var parkourability = Parkourability.get(player);
        var jump = parkourability.get(ChargeJump.class);
        var jumpSkill = (JumpSkill) jump;
        jumpSkill.parcoolskill$setCoolingDown(true);

        if (!CleaveHandler.isAttackReady(player)) return;

        var cleaveAttackDuration = Config.CLEAVE_ATTACK_DURATION.get();
        SkillHandler.use(SkillPayload.Type.CLEAVE_READY, player);
        jumpSkill.parcoolskill$setAttackTime(cleaveAttackDuration);
        jumpSkill.parcoolskill$clearEntityHits();
        event.setCanceled(true);
    }
}
