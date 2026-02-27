package com.xm666.parcoolskill.skill;

import net.minecraft.world.entity.Entity;

public interface JumpSkill {
    int parcoolskill$getNotChargeTick();

    boolean parcoolskill$isAttackReady();

    void parcoolskill$setAttackReady(boolean attackReady);

    int parcoolskill$getAttackTime();

    void parcoolskill$setAttackTime(int attackTime);

    boolean parcoolskill$addEntityHit(Entity entity);

    void parcoolskill$clearEntityHits();

    boolean parcoolskill$isCoolingDown();

    void parcoolskill$setCoolingDown(boolean coolingDown);
}
