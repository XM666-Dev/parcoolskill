package com.xm666.parcoolskill.network;

import com.xm666.parcoolskill.ParCoolSkill;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record SkillParticlePayload(int particleType, int entity) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SkillParticlePayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(ParCoolSkill.MODID, "skill_particle"));
    public static final StreamCodec<ByteBuf, SkillParticlePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            SkillParticlePayload::particleType,
            ByteBufCodecs.VAR_INT,
            SkillParticlePayload::entity,
            SkillParticlePayload::new
    );

    @Override
    public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public enum Type {
        RED,
        GREEN
    }
}
