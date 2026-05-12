package com.xm666.parcoolskill.network;

import com.xm666.parcoolskill.ParCoolSkill;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record TimeScalePayload(
        float scale,
        int duration,
        int transition,
        int targetEntity
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<TimeScalePayload> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(ParCoolSkill.MODID, "time_scale"));
    public static final StreamCodec<ByteBuf, TimeScalePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT,
            TimeScalePayload::scale,
            ByteBufCodecs.VAR_INT,
            TimeScalePayload::duration,
            ByteBufCodecs.VAR_INT,
            TimeScalePayload::transition,
            ByteBufCodecs.VAR_INT,
            TimeScalePayload::targetEntity,
            TimeScalePayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
