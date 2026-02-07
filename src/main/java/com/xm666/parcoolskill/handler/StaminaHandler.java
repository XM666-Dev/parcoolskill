package com.xm666.parcoolskill.handler;

import com.alrex.parcool.api.Stamina;
import com.xm666.parcoolskill.network.StaminaPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class StaminaHandler {
    public static boolean consumeStamina(ServerPlayer player, int value) {
        var stamina = Stamina.get(player);
        if (stamina.getValue() < value) return false;
        PacketDistributor.sendToPlayer(player, new StaminaPayload(-value));
        return true;
    }

    public static boolean recoverStamina(ServerPlayer player, int value) {
        var stamina = Stamina.get(player);
        if (stamina.getValue() == stamina.getMaxValue()) return false;
        PacketDistributor.sendToPlayer(player, new StaminaPayload(value));
        return true;
    }

    public static void handlePayload(final StaminaPayload payload, final IPayloadContext context) {
        var mc = Minecraft.getInstance();
        var player = mc.player;
        var stamina = Stamina.get(player);
        var value = payload.value();
        if (value < 0) {
            stamina.consume(-value);
        } else {
            stamina.recover(value);
        }
    }
}
