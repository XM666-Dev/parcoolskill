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

    boolean parcoolskill$isDisableSliding();

    void parcoolskill$setDisableSliding(boolean disableSliding);

    enum Type {
        DROPKICK,
        HEEL_HOOK,
        LEG_SWEEP,
        NONE
    }
}
