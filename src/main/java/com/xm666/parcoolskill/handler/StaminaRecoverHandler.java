package com.xm666.parcoolskill.handler;

import com.alrex.parcool.api.Stamina;
import com.xm666.parcoolskill.network.StaminaRecoverPayload;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class StaminaRecoverHandler {
    public static void handlePayload(final StaminaRecoverPayload payload, final IPayloadContext context) {
        var player = Minecraft.getInstance().player;
        Stamina.get(player).recover(payload.value());
    }
}
