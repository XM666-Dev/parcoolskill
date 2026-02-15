package com.xm666.parcoolskill.skill;

import org.spongepowered.asm.mixin.Unique;

public interface SlideSkill {
    @Unique
    Type parcoolskill$getReadyType();

    @Unique
    void parcoolskill$setReadyType(Type type);

    @Unique
    int parcoolskill$getInvulnerableTime();

    @Unique
    void parcoolskill$setInvulnerableTime(int invulnerableTime);

    enum Type {
        DROPKICK,
        HEEL_HOOK,
        NONE
    }
}
