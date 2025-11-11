package com.xm666.parcoolskill.network;

import com.xm666.parcoolskill.ParCoolSkill;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record StaminaConsumePayload(int value) implements CustomPacketPayload {
    public static final Type<StaminaConsumePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ParCoolSkill.MODID, "stamina_consume"));
    public static final StreamCodec<ByteBuf, StaminaConsumePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            StaminaConsumePayload::value,
            StaminaConsumePayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
