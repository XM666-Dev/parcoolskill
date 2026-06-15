package com.xm666.parcoolskill.particle;

import com.xm666.parcoolskill.ParCoolSkill;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(ParCoolSkill.MODID)
@EventBusSubscriber(modid = ParCoolSkill.MODID)
public class ParticleTypes {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(
            BuiltInRegistries.PARTICLE_TYPE,
            ParCoolSkill.MODID
    );
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> IRONCLAD_HIT = PARTICLE_TYPES.register(
            "ironclad_hit",
            () -> new SimpleParticleType(false)
    );
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> IRONCLAD_EFFECT = PARTICLE_TYPES.register(
            "ironclad_effect",
            () -> new SimpleParticleType(false)
    );
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SILENT_HIT = PARTICLE_TYPES.register(
            "silent_hit",
            () -> new SimpleParticleType(false)
    );
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SILENT_EFFECT = PARTICLE_TYPES.register(
            "silent_effect",
            () -> new SimpleParticleType(false)
    );
    public static final int IRONCLAD_COLOR = 0xBF4D4D;
    public static final int SILENT_COLOR = 0x4DBF4D;

    public ParticleTypes(IEventBus modEventBus) {
        PARTICLE_TYPES.register(modEventBus);
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ParticleTypes.IRONCLAD_HIT.get(), SkillParticleProvider.with(HitParticle::new, IRONCLAD_COLOR));
        event.registerSpriteSet(ParticleTypes.IRONCLAD_EFFECT.get(), SkillParticleProvider.with(EffectParticle::new, IRONCLAD_COLOR));
        event.registerSpriteSet(ParticleTypes.SILENT_HIT.get(), SkillParticleProvider.with(HitParticle::new, SILENT_COLOR));
        event.registerSpriteSet(ParticleTypes.SILENT_EFFECT.get(), SkillParticleProvider.with(EffectParticle::new, SILENT_COLOR));
    }
}
