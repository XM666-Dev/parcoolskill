package com.xm666.parcoolskill.handler;

import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import com.alrex.parcool.common.action.impl.ChargeJump;
import com.xm666.parcoolskill.ParCoolSkill;
import com.xm666.parcoolskill.skill.JumpSkill;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = ParCoolSkill.MODID)
public class JumpSkillHandler {
    @SubscribeEvent
    public static void onChargeTryToStart(ParCoolActionEvent.TryToStart event) {
        if (!(event.getAction() instanceof ChargeJump jump)) return;

        var jumpSkill = (JumpSkill) jump;
        if (!jumpSkill.parcoolskill$isCoolingDown()) return;

        event.setCanceled(true);
        jumpSkill.parcoolskill$setCoolingDown(jump.getNotChargingTick() < ChargeJump.JUMP_ANIMATION_TICK);
    }
}
