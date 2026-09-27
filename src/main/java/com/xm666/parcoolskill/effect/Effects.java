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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.stream.Stream;

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

    public static void init(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
        POTIONS.register(eventBus);
        eventBus.addListener(Effects::registerBrewingRecipes);
    }

    public static void registerBrewingRecipes(FMLCommonSetupEvent event) {
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
        for (var item : new Item[]{Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION}) {
            var inputIngredient = new Ingredient(Stream.of()) {
                @Override
                public boolean test(@Nullable ItemStack stack) {
                    return stack.is(item) && PotionUtils.getPotion(stack) == input;
                }
            };
            var itemIngredient = Ingredient.of(ingredient);
            var outputStack = PotionUtils.setPotion(new ItemStack(item), output);
            BrewingRecipeRegistry.addRecipe(inputIngredient, itemIngredient, outputStack);
        }
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
