package com.xm666.parcoolskill.network;

import com.xm666.parcoolskill.ParCoolSkill;
import com.xm666.parcoolskill.handler.BulletTimeHandler;
import com.xm666.parcoolskill.handler.ChargeHandler;
import com.xm666.parcoolskill.handler.KickHandler;
import com.xm666.parcoolskill.handler.StaminaHandler;
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
                StaminaConsumePayload.TYPE,
                StaminaConsumePayload.STREAM_CODEC,
                StaminaHandler::handlePayload
        );
        registrar.playToClient(
                StaminaRecoverPayload.TYPE,
                StaminaRecoverPayload.STREAM_CODEC,
                StaminaHandler::handlePayload
        );
        registrar.playToClient(
                BulletTimePayload.TYPE,
                BulletTimePayload.STREAM_CODEC,
                BulletTimeHandler::handlePayload
        );
        registrar.playToClient(
                StopChargePayload.TYPE,
                StopChargePayload.STREAM_CODEC,
                ChargeHandler::handlePayload
        );
    }
}
