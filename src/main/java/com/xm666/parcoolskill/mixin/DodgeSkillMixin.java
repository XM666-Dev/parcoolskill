package com.xm666.parcoolskill.mixin;

import com.alrex.parcool.common.action.impl.Dodge;
import com.xm666.parcoolskill.skill.DodgeSkill;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

public class DodgeSkillMixin {
    @Mixin(Dodge.class)
    public static class DodgeMixin implements DodgeSkill {
        @Unique
        private boolean parcoolskill$attackReady;
        @Unique
        private int parcoolskill$attackReadyTime;

        @Override
        public boolean parcoolskill$isAttackReady() {
            return parcoolskill$attackReady;
        }

        @Override
        public void parcoolskill$setAttackReady(boolean attackReady) {
            parcoolskill$attackReady = attackReady;
        }

        @Override
        public int parcoolskill$getAttackReadyTime() {
            return parcoolskill$attackReadyTime;
        }

        @Override
        public void parcoolskill$setAttackReadyTime(int attackReadyTime) {
            parcoolskill$attackReadyTime = attackReadyTime;
        }
    }
}
