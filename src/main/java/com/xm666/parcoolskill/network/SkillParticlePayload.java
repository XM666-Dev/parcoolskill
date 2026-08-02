package com.xm666.parcoolskill.network;

import com.xm666.parcoolskill.particle.SkillParticleHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SkillParticlePayload(int particleType, int entity) {
    public static void write(SkillParticlePayload msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.particleType);
        buf.writeInt(msg.entity);
    }

    public static SkillParticlePayload read(FriendlyByteBuf buf) {
        return new SkillParticlePayload(
                buf.readInt(),
                buf.readInt()
        );
    }

    public static void handle(SkillParticlePayload msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> SkillParticleHandler.handlePayload(msg, ctx))
        );
        ctx.get().setPacketHandled(true);
    }

    public enum Type {
        IRONCLAD_HIT,
        IRONCLAD_EFFECT,
        SILENT_HIT,
        SILENT_EFFECT
    }
}
