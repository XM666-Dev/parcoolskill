package com.xm666.parcoolskill.handler;

import com.xm666.parcoolskill.network.SkillParticlePayload;
import com.xm666.parcoolskill.particle.ParticleTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SkillParticleHandler {
    public static void handlePayload(final SkillParticlePayload payload, final IPayloadContext context) {
        var level = context.player().level();
        var entity = level.getEntity(payload.entity());
        if (entity == null) return;

        var mc = Minecraft.getInstance();
        var type = SkillParticlePayload.Type.values()[payload.particleType()];
        var particleType = (switch (type) {
            case RED -> ParticleTypes.RED_SKILL;
            case GREEN -> ParticleTypes.GREEN_SKILL;
        }).get();
        mc.particleEngine.createTrackingEmitter(entity, particleType);
    }

    public static void emit(SkillParticlePayload.Type type, Entity entity) {
        if (entity.level().isClientSide()) return;

        PacketDistributor.sendToPlayersTrackingEntity(entity, new SkillParticlePayload(type.ordinal(), entity.getId()));
    }
}
