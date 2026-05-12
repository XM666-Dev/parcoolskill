package com.xm666.parcoolskill.damage;

import com.xm666.parcoolskill.ParCoolSkill;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

public class DamageTypes {
    public static final ResourceKey<DamageType> SLIDE_ATTACK = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            Identifier.fromNamespaceAndPath(ParCoolSkill.MODID, "slide_attack")
    );
}
