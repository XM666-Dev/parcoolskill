package com.xm666.parcoolskill.handler;

import com.xm666.parcoolskill.ParCoolSkill;
import com.xm666.parcoolskill.network.SkillAttackPayload;
import com.xm666.parcoolskill.network.StaminaPayload;
import com.xm666.parcoolskill.network.TimeScalePayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@EventBusSubscriber(modid = ParCoolSkill.MODID)
public class PayloadHandler {
    @SubscribeEvent
    static void register(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");
        registrar.playToServer(
                SkillAttackPayload.TYPE,
                SkillAttackPayload.STREAM_CODEC,
                SkillAttackHandler::handlePayload
        );
        registrar.playToClient(
                StaminaPayload.TYPE,
                StaminaPayload.STREAM_CODEC,
                StaminaHandler::handlePayload
        );
        registrar.playToClient(
                TimeScalePayload.TYPE,
                TimeScalePayload.STREAM_CODEC,
                TimeScaleHandler::handlePayload
        );
    }
}
