package com.xm666.parcoolskill.handler;

import com.alrex.parcool.api.Stamina;
import com.xm666.parcoolskill.network.StaminaConsumePayload;
import com.xm666.parcoolskill.network.StaminaRecoverPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class StaminaHandler {
    public static boolean consumeStamina(ServerPlayer player, int value) {
        var stamina = Stamina.get(player);
        if (stamina.getValue() < value) return false;
        PacketDistributor.sendToPlayer(player, new StaminaConsumePayload(value));
        return true;
    }

    public static void handlePayload(final StaminaConsumePayload payload, final IPayloadContext context) {
        var player = Minecraft.getInstance().player;
        Stamina.get(player).consume(payload.value());
    }

    public static void handlePayload(final StaminaRecoverPayload payload, final IPayloadContext context) {
        var player = Minecraft.getInstance().player;
        Stamina.get(player).recover(payload.value());
    }
}
