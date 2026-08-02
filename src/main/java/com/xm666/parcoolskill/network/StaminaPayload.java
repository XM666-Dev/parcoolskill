package com.xm666.parcoolskill.network;

import com.xm666.parcoolskill.handler.StaminaHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record StaminaPayload(int value) {
    public static void write(StaminaPayload msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.value);
    }

    public static StaminaPayload read(FriendlyByteBuf buf) {
        return new StaminaPayload(
                buf.readInt()
        );
    }

    public static void handle(StaminaPayload msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> StaminaHandler.handlePayload(msg, ctx))
        );
        ctx.get().setPacketHandled(true);
    }
}
