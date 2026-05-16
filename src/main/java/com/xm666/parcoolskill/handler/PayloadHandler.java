package com.xm666.parcoolskill.handler;

import com.xm666.parcoolskill.ParCoolSkill;
import com.xm666.parcoolskill.network.SkillParticlePayload;
import com.xm666.parcoolskill.network.SkillPayload;
import com.xm666.parcoolskill.network.StaminaPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@EventBusSubscriber(modid = ParCoolSkill.MODID)
public class PayloadHandler {
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");
        registrar.playToServer(
                SkillPayload.TYPE,
                SkillPayload.STREAM_CODEC,
                SkillHandler::handlePayload
        );
        registrar.playToClient(
                StaminaPayload.TYPE,
                StaminaPayload.STREAM_CODEC,
                StaminaHandler::handlePayload
        );
        registrar.playToClient(
                SkillParticlePayload.TYPE,
                SkillParticlePayload.STREAM_CODEC,
                SkillParticleHandler::handlePayload
        );
    }
}
