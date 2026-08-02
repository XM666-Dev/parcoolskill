package com.xm666.parcoolskill.network;

import com.xm666.parcoolskill.handler.SkillHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SkillPayload(int skillType, int sourceEntity, int targetEntity) {
    public static void write(SkillPayload msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.skillType);
        buf.writeInt(msg.sourceEntity);
        buf.writeInt(msg.targetEntity);
    }

    public static SkillPayload read(FriendlyByteBuf buf) {
        return new SkillPayload(
                buf.readInt(),
                buf.readInt(),
                buf.readInt()
        );
    }

    public static void handle(SkillPayload msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            SkillHandler.handlePayload(msg, ctx);
        });
        ctx.get().setPacketHandled(true);
    }

    public enum Type {
        DROPKICK,
        HEEL_HOOK,
        CLEAVE_READY,
        CLEAVE_ATTACK
    }
}
