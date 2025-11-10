package com.xm666.parcoolskill.network;

import com.xm666.parcoolskill.ParCoolSkill;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record BulletTimePayload(float timeScale, int timeScaleTicks) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<BulletTimePayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(ParCoolSkill.MODID, "bullet_time"));
    public static final StreamCodec<ByteBuf, BulletTimePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT,
            BulletTimePayload::timeScale,
            ByteBufCodecs.VAR_INT,
            BulletTimePayload::timeScaleTicks,
            BulletTimePayload::new
    );

    @Override
    public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
