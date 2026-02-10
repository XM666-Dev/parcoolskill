package com.xm666.parcoolskill.network;

import com.xm666.parcoolskill.ParCoolSkill;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record TimeScalePayload(float scale, int scaleTicks) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<TimeScalePayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(ParCoolSkill.MODID, "time_scale"));
    public static final StreamCodec<ByteBuf, TimeScalePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT,
            TimeScalePayload::scale,
            ByteBufCodecs.VAR_INT,
            TimeScalePayload::scaleTicks,
            TimeScalePayload::new
    );

    @Override
    public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
