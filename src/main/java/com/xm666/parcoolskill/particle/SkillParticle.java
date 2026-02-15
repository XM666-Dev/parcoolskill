package com.xm666.parcoolskill.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.CritParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

public class SkillParticle extends CritParticle {
    public SkillParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet spriteSet) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.setSpriteFromAge(spriteSet);
    }

    public record RedProvider(SpriteSet spriteSet) implements ParticleProvider<SimpleParticleType> {
        @Override
        public Particle createParticle(@NotNull SimpleParticleType type, @NotNull ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            SkillParticle skillParticle = new SkillParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet);
            skillParticle.setColor(0.75F, 0.2F, 0.2F);
            return skillParticle;
        }
    }

    public record GreenProvider(SpriteSet spriteSet) implements ParticleProvider<SimpleParticleType> {
        @Override
        public Particle createParticle(@NotNull SimpleParticleType type, @NotNull ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            SkillParticle skillParticle = new SkillParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet);
            skillParticle.setColor(0.2F, 0.75F, 0.2F);
            return skillParticle;
        }
    }
}
