package com.xm666.parcoolskill.particle;

import com.mojang.datafixers.util.Function8;
import com.xm666.parcoolskill.handler.SkillHandler;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

public record SkillParticleProvider(
        SpriteSet spriteSet,
        Function8<ClientLevel, Double, Double, Double, Double, Double, Double, TextureAtlasSprite, SingleQuadParticle> particleConstructor,
        int color
) implements ParticleProvider<SimpleParticleType> {
    public static ParticleResources.SpriteParticleRegistration<SimpleParticleType> with(
            Function8<ClientLevel, Double, Double, Double, Double, Double, Double, TextureAtlasSprite, SingleQuadParticle> particleConstructor,
            int color
    ) {
        return (SpriteSet spriteSet) -> new SkillParticleProvider(spriteSet, particleConstructor, color);
    }

    @Override
    public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, RandomSource randomSource) {
        var particle = particleConstructor.apply(clientLevel, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet.get(randomSource));
        var red = SkillHandler.getRedComponent(color);
        var green = SkillHandler.getGreenComponent(color);
        var blue = SkillHandler.getBlueComponent(color);
        particle.setColor(red, green, blue);
        return particle;
    }
}
