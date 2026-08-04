package com.xm666.parcoolskill.damage;

import com.xm666.parcoolskill.ParCoolSkill;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

public class DamageTypes {
    public static final ResourceKey<DamageType> KICK_ATTACK = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            new ResourceLocation(ParCoolSkill.MODID, "kick_attack")
    );
    public static final ResourceKey<DamageType> TWIST_ATTACK = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            new ResourceLocation(ParCoolSkill.MODID, "twist_attack")
    );
    public static final TagKey<DamageType> IS_PHYSICAL = TagKey.create(
            Registries.DAMAGE_TYPE,
            new ResourceLocation(ParCoolSkill.MODID, "is_physical")
    );
    public static final TagKey<DamageType> IS_MAGIC = TagKey.create(
            Registries.DAMAGE_TYPE,
            new ResourceLocation(ParCoolSkill.MODID, "is_magic")
    );
}
