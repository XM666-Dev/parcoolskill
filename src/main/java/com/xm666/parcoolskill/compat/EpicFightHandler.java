package com.xm666.parcoolskill.compat;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;

public class EpicFightHandler {
    private static final TagKey<DamageType> IS_MELEE = TagKey.create(
            Registries.DAMAGE_TYPE,
            new ResourceLocation("epicfight", "is_melee")
    );

    public static boolean isEpicFightAttack(DamageSource source) {
        return source.is(IS_MELEE);
    }
}
