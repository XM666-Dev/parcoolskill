package com.xm666.parcoolskill.network;

import com.xm666.parcoolskill.ParCoolSkill;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record StaminaRecoverPayload(int value) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<StaminaRecoverPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(ParCoolSkill.MODID, "stamina_recover"));
    public static final StreamCodec<ByteBuf, StaminaRecoverPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            StaminaRecoverPayload::value,
            StaminaRecoverPayload::new
    );

    @Override
    public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
