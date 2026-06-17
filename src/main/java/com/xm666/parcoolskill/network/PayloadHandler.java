package com.xm666.parcoolskill.network;

import com.xm666.parcoolskill.ParCoolSkill;
import com.xm666.parcoolskill.handler.SkillHandler;
import com.xm666.parcoolskill.handler.StaminaHandler;
import com.xm666.parcoolskill.particle.SkillParticleHandler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@EventBusSubscriber(modid = ParCoolSkill.MODID)
public class PayloadHandler {
    @SubscribeEvent
    public static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
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
