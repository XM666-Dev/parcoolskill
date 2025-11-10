package com.xm666.parcoolskill.network;

import com.xm666.parcoolskill.ParCoolSkill;
import com.xm666.parcoolskill.handler.BulletTimeHandler;
import com.xm666.parcoolskill.handler.KickHandler;
import com.xm666.parcoolskill.handler.StaminaRecoverHandler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@EventBusSubscriber(modid = ParCoolSkill.MODID)
public class Network {
    @SubscribeEvent
    static void register(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");
        registrar.playToServer(
                KickPayload.TYPE,
                KickPayload.STREAM_CODEC,
                KickHandler::handlePayload
        );
        registrar.playToClient(
                StaminaRecoverPayload.TYPE,
                StaminaRecoverPayload.STREAM_CODEC,
                StaminaRecoverHandler::handlePayload
        );
        registrar.playToClient(
                BulletTimePayload.TYPE,
                BulletTimePayload.STREAM_CODEC,
                BulletTimeHandler::handlePayload
        );
    }
}
