package com.xm666.parcoolskill.handler;

import com.alrex.parcool.api.Stamina;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.xm666.parcoolskill.network.StaminaPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class StaminaHandler {
    public static void handlePayload(final StaminaPayload payload, final IPayloadContext context) {
        var player = context.player();
        var stamina = Stamina.get(player);
        var value = payload.value();
        if (value < 0) {
            stamina.consume(-value);
        } else {
            stamina.recover(value);
        }
    }

    public static void consume(Player player, int value) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        PacketDistributor.sendToPlayer(serverPlayer, new StaminaPayload(-value));
    }

    public static void recover(Player player, int value) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        PacketDistributor.sendToPlayer(serverPlayer, new StaminaPayload(value));
    }

    public static int getConsumptionOf(Player player, Class<? extends Action> action) {
        var parkourability = Parkourability.get(player);
        return parkourability.getActionInfo().getStaminaConsumptionOf(action);
    }
}
