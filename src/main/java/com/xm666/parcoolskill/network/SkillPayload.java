package com.xm666.parcoolskill.network;

import com.xm666.parcoolskill.ParCoolSkill;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SkillPayload(int skillType, int sourceEntity, int targetEntity) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SkillPayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(ParCoolSkill.MODID, "skill")
    );
    public static final StreamCodec<ByteBuf, SkillPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            SkillPayload::skillType,
            ByteBufCodecs.VAR_INT,
            SkillPayload::sourceEntity,
            ByteBufCodecs.VAR_INT,
            SkillPayload::targetEntity,
            SkillPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public enum Type {
        DROPKICK,
        HEEL_HOOK,
        CLEAVE_READY,
        CLEAVE_ATTACK
    }
}
