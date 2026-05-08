package com.xm666.parcoolskill.network;

import com.xm666.parcoolskill.ParCoolSkill;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record StaminaPayload(int value) implements CustomPacketPayload {
    public static final Type<StaminaPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ParCoolSkill.MODID, "stamina"));
    public static final StreamCodec<ByteBuf, StaminaPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            StaminaPayload::value,
            StaminaPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
