package com.xm666.parcoolskill.handler;

import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import com.alrex.parcool.common.action.impl.ChargeJump;
import com.xm666.parcoolskill.ParCoolSkill;
import com.xm666.parcoolskill.action.SkillJump;
import com.xm666.parcoolskill.network.StopChargePayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@EventBusSubscriber(modid = ParCoolSkill.MODID)
public class ChargeHandler {
    private static boolean stopCharge = false;
    private static boolean cooldown = false;

    public static void handlePayload(final StopChargePayload payload, final IPayloadContext context) {
        stopCharge = true;
    }

    @SubscribeEvent
    static void onChargeTryToStart(ParCoolActionEvent.TryToStartEvent event) {
        if (!(event.getAction() instanceof SkillJump jump)) return;
        if (stopCharge) {
            event.setCanceled(true);
            cooldown = true;
            stopCharge = false;
        }
        if (jump.parcoolskill$getNotChargeTick() >= ChargeJump.JUMP_ANIMATION_TICK) {
            cooldown = false;
        }
        if (cooldown) {
            event.setCanceled(true);
        }
    }
}
