package com.xm666.parcoolskill.network;

import com.xm666.parcoolskill.ParCoolSkill;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record SkillAttackPayload(int targetEntityId, int sourceEntityId,
                                 int skillAttackType) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SkillAttackPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(ParCoolSkill.MODID, "kick"));
    public static final StreamCodec<ByteBuf, SkillAttackPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            SkillAttackPayload::targetEntityId,
            ByteBufCodecs.VAR_INT,
            SkillAttackPayload::sourceEntityId,
            ByteBufCodecs.VAR_INT,
            SkillAttackPayload::skillAttackType,
            SkillAttackPayload::new
    );

    @Override
    public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public enum SkillAttackType {
        DROPKICK,
        HEEL_HOOK,
        CLEAVE
    }
}
