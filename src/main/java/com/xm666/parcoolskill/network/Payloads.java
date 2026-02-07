package com.xm666.parcoolskill.network;

import com.xm666.parcoolskill.ParCoolSkill;
import com.xm666.parcoolskill.handler.BulletTimeHandler;
import com.xm666.parcoolskill.handler.SkillAttackHandler;
import com.xm666.parcoolskill.handler.StaminaHandler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@EventBusSubscriber(modid = ParCoolSkill.MODID)
public class Payloads {
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
                BulletTimePayload.TYPE,
                BulletTimePayload.STREAM_CODEC,
                BulletTimeHandler::handlePayload
        );
    }
}
