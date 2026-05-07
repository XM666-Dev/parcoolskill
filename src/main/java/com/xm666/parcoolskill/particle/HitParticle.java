package com.xm666.parcoolskill.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.CritParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class HitParticle extends CritParticle {
    public HitParticle(ClientLevel clientLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, TextureAtlasSprite randomSource) {
        super(clientLevel, x, y, z, xSpeed, ySpeed, zSpeed, randomSource);
    }
}
