package com.xm666.parcoolskill.effect;

import com.xm666.parcoolskill.ParCoolSkill;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(ParCoolSkill.MODID)
@EventBusSubscriber(modid = ParCoolSkill.MODID)
public class Effects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(
            Registries.MOB_EFFECT,
            ParCoolSkill.MODID
    );

    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(
            Registries.POTION,
            ParCoolSkill.MODID
    );

    public static final DeferredHolder<MobEffect, Vulnerable> VULNERABLE = MOB_EFFECTS.register(
            "vulnerable",
            () -> new Vulnerable(MobEffectCategory.HARMFUL, 0x736156)
    );
    public static final Holder<Potion> VULNERABLE_POTION = POTIONS.register(
            "vulnerable",
            () -> new Potion(new MobEffectInstance(VULNERABLE, 420))
    );
    public static final Holder<Potion> LONG_VULNERABLE_POTION = POTIONS.register(
            "long_vulnerable",
            () -> new Potion(new MobEffectInstance(VULNERABLE, 600))
    );
    public static final DeferredHolder<MobEffect, Neutralized> NEUTRALIZED = MOB_EFFECTS.register(
            "neutralized",
            () -> new Neutralized(MobEffectCategory.HARMFUL, 0x484D48)
    );
    public static final Holder<Potion> NEUTRALIZED_POTION = POTIONS.register(
            "neutralized",
            () -> new Potion(new MobEffectInstance(NEUTRALIZED, 420))
    );

    public static final Holder<Potion> LONG_NEUTRALIZED_POTION = POTIONS.register(
            "long_neutralized",
            () -> new Potion(new MobEffectInstance(NEUTRALIZED, 600))
    );

    public Effects(IEventBus modEventBus) {
        MOB_EFFECTS.register(modEventBus);
        POTIONS.register(modEventBus);
    }

    @SubscribeEvent
    public static void registerBrewingRecipes(RegisterBrewingRecipesEvent event) {
        var builder = event.getBuilder();
        builder.addMix(
                Potions.WEAKNESS,
                Items.WITHER_ROSE,
                VULNERABLE_POTION
        );
        builder.addMix(
                Potions.LONG_WEAKNESS,
                Items.WITHER_ROSE,
                LONG_VULNERABLE_POTION
        );
        builder.addMix(
                VULNERABLE_POTION,
                Items.REDSTONE,
                LONG_VULNERABLE_POTION
        );
        builder.addMix(
                Potions.WEAKNESS,
                Items.COBWEB,
                NEUTRALIZED_POTION
        );
        builder.addMix(
                Potions.LONG_WEAKNESS,
                Items.COBWEB,
                LONG_NEUTRALIZED_POTION
        );
        builder.addMix(
                NEUTRALIZED_POTION,
                Items.REDSTONE,
                LONG_NEUTRALIZED_POTION
        );
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
