package com.xm666.parcoolskill.network;

import com.xm666.parcoolskill.ParCoolSkill;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record KickPayload(int targetId, int sourceId, int kickType) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<KickPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(ParCoolSkill.MODID, "kick"));
    public static final StreamCodec<ByteBuf, KickPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            KickPayload::targetId,
            ByteBufCodecs.VAR_INT,
            KickPayload::sourceId,
            ByteBufCodecs.VAR_INT,
            KickPayload::kickType,
            KickPayload::new
    );

    @Override
    public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public enum Type {
        DROPKICK,
        SLIDEKICK
    }
}
