package com.xm666.parcoolskill.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.CritParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.Mth;

public class HitParticle extends CritParticle {
    public HitParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.setSpriteFromAge(sprites);
    }

    public int getLightColor(float partialTick) {
        var f = ((float) this.age + partialTick) / (float) this.lifetime;
        f = Mth.clamp(f, 0.0F, 1.0F);
        var i = super.getLightColor(partialTick);
        var j = i & 255;
        var k = i >> 16 & 255;
        j += (int) (f * 15.0F * 16.0F);
        if (j > 240) {
            j = 240;
        }

        return j | k << 16;
    }
}
