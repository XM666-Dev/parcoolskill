package com.xm666.parcoolskill.skill;

import org.spongepowered.asm.mixin.Unique;

public interface SlideSkill {
    @Unique
    ReadyAttackType parcoolskill$getReadyAttackType();

    @Unique
    void parcoolskill$setReadyAttackType(ReadyAttackType readyAttackType);

    @Unique
    int parcoolskill$getInvulnerableTime();

    @Unique
    void parcoolskill$setInvulnerableTime(int invulnerableTime);

    enum ReadyAttackType {
        DROPKICK,
        HEEL_HOOK,
        NONE
    }
}
