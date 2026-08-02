package com.xm666.parcoolskill.effect;

import com.xm666.parcoolskill.ParCoolSkill;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = ParCoolSkill.MODID)
public class Effects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(
            Registries.MOB_EFFECT,
            ParCoolSkill.MODID
    );
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(
            Registries.POTION,
            ParCoolSkill.MODID
    );
    public static final RegistryObject<Vulnerable> VULNERABLE = MOB_EFFECTS.register(
            "vulnerable",
            () -> new Vulnerable(MobEffectCategory.HARMFUL, 0x736156)
    );
    public static final RegistryObject<Potion> VULNERABLE_POTION = POTIONS.register(
            "vulnerable",
            () -> new Potion(new MobEffectInstance(VULNERABLE.get(), 420))
    );
    public static final RegistryObject<Potion> LONG_VULNERABLE_POTION = POTIONS.register(
            "long_vulnerable",
            () -> new Potion(new MobEffectInstance(VULNERABLE.get(), 600))
    );
    public static final RegistryObject<Neutralized> NEUTRALIZED = MOB_EFFECTS.register(
            "neutralized",
            () -> new Neutralized(MobEffectCategory.HARMFUL, 0x484D48)
    );
    public static final RegistryObject<Potion> NEUTRALIZED_POTION = POTIONS.register(
            "neutralized",
            () -> new Potion(new MobEffectInstance(NEUTRALIZED.get(), 420))
    );
    public static final RegistryObject<Potion> LONG_NEUTRALIZED_POTION = POTIONS.register(
            "long_neutralized",
            () -> new Potion(new MobEffectInstance(NEUTRALIZED.get(), 600))
    );

    public static void init(IEventBus modEventBus) {
        MOB_EFFECTS.register(modEventBus);
        POTIONS.register(modEventBus);
    }

    public static void registerBrewingRecipes() {
        addMix(
                Potions.WEAKNESS,
                Items.WITHER_ROSE,
                VULNERABLE_POTION.get()
        );
        addMix(
                Potions.LONG_WEAKNESS,
                Items.WITHER_ROSE,
                LONG_VULNERABLE_POTION.get()
        );
        addMix(
                VULNERABLE_POTION.get(),
                Items.REDSTONE,
                LONG_VULNERABLE_POTION.get()
        );
        addMix(
                Potions.WEAKNESS,
                Items.COBWEB,
                NEUTRALIZED_POTION.get()
        );
        addMix(
                Potions.LONG_WEAKNESS,
                Items.COBWEB,
                LONG_NEUTRALIZED_POTION.get()
        );
        addMix(
                NEUTRALIZED_POTION.get(),
                Items.REDSTONE,
                LONG_NEUTRALIZED_POTION.get()
        );
    }

    private static void addMix(Potion input, Item ingredient, Potion output) {
        BrewingRecipeRegistry.addRecipe(new IBrewingRecipe() {
            @Override
            public boolean isInput(ItemStack stack) {
                return stack.is(Items.POTION) && PotionUtils.getPotion(stack) == input;
            }

            @Override
            public boolean isIngredient(ItemStack stack) {
                return stack.is(ingredient);
            }

            @Override
            public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
                var potion = new ItemStack(Items.POTION);
                PotionUtils.setPotion(potion, output);
                return potion;
            }
        });
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
