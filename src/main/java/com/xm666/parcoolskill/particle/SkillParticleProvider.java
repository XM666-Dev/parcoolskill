package com.xm666.parcoolskill.particle;

import com.mojang.datafixers.util.Function8;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public record SkillParticleProvider(
        Function8<ClientLevel, Double, Double, Double, Double, Double, Double, SpriteSet, TextureSheetParticle> particleConstructor,
        int color,
        SpriteSet spriteSet
) implements ParticleProvider<SimpleParticleType> {
    public static ParticleEngine.SpriteParticleRegistration<SimpleParticleType> with(
            Function8<ClientLevel, Double, Double, Double, Double, Double, Double, SpriteSet, TextureSheetParticle> particleConstructor,
            int color
    ) {
        return spriteSet -> new SkillParticleProvider(particleConstructor, color, spriteSet);
    }

    @Override
    public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        var particle = particleConstructor.apply(level, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet);
        var red = SkillParticleHandler.getRedComponent(color);
        var green = SkillParticleHandler.getGreenComponent(color);
        var blue = SkillParticleHandler.getBlueComponent(color);
        particle.setColor(red, green, blue);
        return particle;
    }
}
