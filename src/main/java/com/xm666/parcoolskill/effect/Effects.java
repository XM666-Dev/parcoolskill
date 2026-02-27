package com.xm666.parcoolskill.effect;

import com.xm666.parcoolskill.ParCoolSkill;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(ParCoolSkill.MODID)
public class Effects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(
            Registries.MOB_EFFECT,
            ParCoolSkill.MODID
    );

    public static final DeferredHolder<MobEffect, Vulnerable> VULNERABLE = MOB_EFFECTS.register(
            "vulnerable",
            () -> new Vulnerable(MobEffectCategory.HARMFUL, 0x736156)
    );

    public static final DeferredHolder<MobEffect, Neutralized> NEUTRALIZED = MOB_EFFECTS.register(
            "neutralized",
            () -> new Neutralized(MobEffectCategory.HARMFUL, 0x484D48)
    );

    public Effects(IEventBus modEventBus) {
        MOB_EFFECTS.register(modEventBus);
    }

    public static class Vulnerable extends MobEffect {
        public Vulnerable(MobEffectCategory category, int color) {
            super(category, color);
        }
    }

    public static class Neutralized extends MobEffect {
        public Neutralized(MobEffectCategory category, int color) {
            super(category, color);
        }
    }
}
