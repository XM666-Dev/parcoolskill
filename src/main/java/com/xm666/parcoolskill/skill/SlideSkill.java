package com.xm666.parcoolskill.skill;

public interface SlideSkill {
    Type parcoolskill$getReadyType();

    void parcoolskill$setReadyType(Type type);

    int parcoolskill$getInvulnerableTime();

    void parcoolskill$setInvulnerableTime(int invulnerableTime);

    boolean parcoolskill$isInvulnerable();

    void parcoolskill$setInvulnerable(boolean invulnerable);

    enum Type {
        DROPKICK,
        HEEL_HOOK,
        NONE
    }
}
