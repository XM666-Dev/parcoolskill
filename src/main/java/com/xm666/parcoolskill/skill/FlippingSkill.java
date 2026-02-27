package com.xm666.parcoolskill.skill;

import org.spongepowered.asm.mixin.Unique;

public interface FlippingSkill {
    @Unique
    int parcoolskill$getSkillTime();

    @Unique
    void parcoolskill$setSkillTime(int skillTime);

    @Unique
    int parcoolskill$getCooldown();

    @Unique
    void parcoolskill$setCooldown(int cooldown);
}
