package com.xm666.parcoolskill.particle;

import com.xm666.parcoolskill.ParCoolSkill;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = ParCoolSkill.MODID)
public class ParticleTypes {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(
            ForgeRegistries.PARTICLE_TYPES,
            ParCoolSkill.MODID
    );
    public static final RegistryObject<SimpleParticleType> IRONCLAD_HIT = PARTICLE_TYPES.register(
            "ironclad_hit",
            () -> new SimpleParticleType(false)
    );
    public static final RegistryObject<SimpleParticleType> IRONCLAD_EFFECT = PARTICLE_TYPES.register(
            "ironclad_effect",
            () -> new SimpleParticleType(false)
    );
    public static final RegistryObject<SimpleParticleType> SILENT_HIT = PARTICLE_TYPES.register(
            "silent_hit",
            () -> new SimpleParticleType(false)
    );
    public static final RegistryObject<SimpleParticleType> SILENT_EFFECT = PARTICLE_TYPES.register(
            "silent_effect",
            () -> new SimpleParticleType(false)
    );
    public static final int IRONCLAD_COLOR = 0xBF4D4D;
    public static final int SILENT_COLOR = 0x4DBF4D;

    public static void init(IEventBus modEventBus) {
        PARTICLE_TYPES.register(modEventBus);
        modEventBus.addListener(ParticleTypes::registerParticleProviders);
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(IRONCLAD_HIT.get(), SkillParticleProvider.with(HitParticle::new, IRONCLAD_COLOR));
        event.registerSpriteSet(IRONCLAD_EFFECT.get(), SkillParticleProvider.with(EffectParticle::new, IRONCLAD_COLOR));
        event.registerSpriteSet(SILENT_HIT.get(), SkillParticleProvider.with(HitParticle::new, SILENT_COLOR));
        event.registerSpriteSet(SILENT_EFFECT.get(), SkillParticleProvider.with(EffectParticle::new, SILENT_COLOR));
    }
}
