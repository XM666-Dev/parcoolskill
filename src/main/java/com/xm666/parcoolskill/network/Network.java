package com.xm666.parcoolskill.network;

import com.xm666.parcoolskill.ParCoolSkill;
import com.xm666.parcoolskill.handler.KickHandler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;

@EventBusSubscriber(modid = ParCoolSkill.MODID)
public class Network {
    @SubscribeEvent
    static void register(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");
        registrar.playBidirectional(
                KickPayload.TYPE,
                KickPayload.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        KickHandler::handlePayload,
                        KickHandler::handlePayload
                )
        );
    }
}
