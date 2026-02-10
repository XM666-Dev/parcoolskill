package com.xm666.parcoolskill.handler;

import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import com.xm666.parcoolskill.Config;
import com.xm666.parcoolskill.ParCoolSkill;
import com.xm666.parcoolskill.skill.JumpSkill;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = ParCoolSkill.MODID)
public class ChargeCooldownHandler {
    public static boolean cooldown = false;

    @SubscribeEvent
    static void onChargeTryToStart(ParCoolActionEvent.TryToStart event) {
        if (!(event.getAction() instanceof JumpSkill jump)) return;

        var cleaveCooldownDuration = Config.CLEAVE_COOLDOWN_DURATION.get();
        cooldown &= jump.parcoolskill$getNotChargeTick() < cleaveCooldownDuration;
        if (!cooldown) return;

        event.setCanceled(true);
    }
}
