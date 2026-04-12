package com.xm666.parcoolskill.skill;

public interface FlippingSkill {
    int parcoolskill$getSkillTime();

    void parcoolskill$setSkillTime(int skillTime);

    int parcoolskill$getCooldown();

    void parcoolskill$setCooldown(int cooldown);

    boolean parcoolskill$isAttackReady();

    void parcoolskill$setAttackReady(boolean attackReady);

    int parcoolskill$getInvulnerableTime();

    void parcoolskill$setInvulnerableTime(int invulnerableTime);

    boolean parcoolskill$disableCrit();

    void parcoolskill$setDisableCrit(boolean disableCrit);
}
