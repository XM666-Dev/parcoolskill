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

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> RED_SKILL = PARTICLE_TYPES.register(
            "red_skill",
            () -> new SimpleParticleType(false)
    );

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> GREEN_SKILL = PARTICLE_TYPES.register(
            "green_skill",
            () -> new SimpleParticleType(false)
    );

    public ParticleTypes(IEventBus modEventBus) {
        PARTICLE_TYPES.register(modEventBus);
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ParticleTypes.RED_SKILL.get(), SkillParticle.RedProvider::new);
        event.registerSpriteSet(ParticleTypes.GREEN_SKILL.get(), SkillParticle.GreenProvider::new);
    }
}
