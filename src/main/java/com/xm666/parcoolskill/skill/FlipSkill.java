package com.xm666.parcoolskill.skill;

public interface FlipSkill {
    int parcoolskill$getSkillTime();

    void parcoolskill$setSkillTime(int skillTime);

    int parcoolskill$getCooldown();

    void parcoolskill$setCooldown(int cooldown);

    Type parcoolskill$getReadyType();

    void parcoolskill$setReadyType(Type type);

    int parcoolskill$getInvulnerableTime();

    void parcoolskill$setInvulnerableTime(int invulnerableTime);

    enum Type {
        VAULT,
        ATTACK,
        NONE
    }
}
