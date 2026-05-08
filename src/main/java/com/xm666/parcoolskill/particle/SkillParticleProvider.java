package com.xm666.parcoolskill.particle;

import com.mojang.datafixers.util.Function8;
import com.xm666.parcoolskill.handler.SkillHandler;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

public record SkillParticleProvider(
        SpriteSet spriteSet,
        Function8<ClientLevel, Double, Double, Double, Double, Double, Double, SpriteSet, TextureSheetParticle> particleConstructor,
        int color
) implements ParticleProvider<SimpleParticleType> {
    public static ParticleEngine.SpriteParticleRegistration<SimpleParticleType> with(
            Function8<ClientLevel, Double, Double, Double, Double, Double, Double, SpriteSet, TextureSheetParticle> particleConstructor,
            int color
    ) {
        return (SpriteSet spriteSet) -> new SkillParticleProvider(spriteSet, particleConstructor, color);
    }

    @Override
    public Particle createParticle(SimpleParticleType type, @NotNull ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        var particle = particleConstructor.apply(level, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet);
        var red = SkillHandler.getRedComponent(color);
        var green = SkillHandler.getGreenComponent(color);
        var blue = SkillHandler.getBlueComponent(color);
        particle.setColor(red, green, blue);
        return particle;
    }
}
