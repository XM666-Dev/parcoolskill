package com.xm666.parcoolskill.network;

import com.xm666.parcoolskill.ParCoolSkill;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record StopChargePayload() implements CustomPacketPayload {
    public static final StopChargePayload INSTANCE = new StopChargePayload();
    public static final Type<StopChargePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ParCoolSkill.MODID, "stop_charge"));
    public static final StreamCodec<ByteBuf, StopChargePayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
