package com.xm666.parcoolskill.handler;

import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import com.alrex.parcool.common.action.impl.CatLeap;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.xm666.parcoolskill.ParCoolSkill;
import com.xm666.parcoolskill.event.PlayerAttackEvent;
import com.xm666.parcoolskill.skill.LeapSkill;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = ParCoolSkill.MODID)
public class LeapSkillHandler {
    @SubscribeEvent
    static void onLeapStart(ParCoolActionEvent.Start.Pre event) {
        if (!(event.getAction() instanceof LeapSkill leapSkill)) return;

        leapSkill.parcoolskill$setAttackReady(true);
    }

    @SubscribeEvent
    static void onLeapFinish(ParCoolActionEvent.Finish.Pre event) {
        if (!(event.getAction() instanceof LeapSkill leapSkill)) return;

        leapSkill.parcoolskill$setAttackReady(false);
    }

    @SubscribeEvent
    static void onLeapTick(ParCoolActionEvent.Tick.Pre event) {
        if (!(event.getAction() instanceof LeapSkill leapSkill)) return;

        var parryTime = leapSkill.parcoolskill$getParryTime();
        if (parryTime == 0) return;

        leapSkill.parcoolskill$setParryTime(parryTime - 1);
    }

    @SubscribeEvent
    static void onPlayerAttack(PlayerAttackEvent.Pre event) {
        var player = event.getEntity();

        var skillLeap = (LeapSkill) Parkourability.get(player).get(CatLeap.class);
        if (!skillLeap.parcoolskill$isAttackReady()) return;
        skillLeap.parcoolskill$setAttackReady(false);

        if (!event.isFullStrength()) return;

        event.setAmount(event.getAmount() * 2.0F);
        skillLeap.parcoolskill$setParryTime(20);
    }
}
