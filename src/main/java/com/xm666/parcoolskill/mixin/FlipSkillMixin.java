package com.xm666.parcoolskill.mixin;

import com.alrex.parcool.common.action.impl.Flipping;
import com.xm666.parcoolskill.skill.FlipSkill;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

public class FlipSkillMixin {
    @Mixin(Flipping.class)
    private static class FlippingMixin implements FlipSkill {
        @Unique
        private int parcoolskill$skillTime;

        @Unique
        private int parcoolskill$cooldown;

        @Unique
        private Type parcoolskill$readyType = Type.NONE;

        @Unique
        private int parcoolskill$invulnerableTime;

        @Override
        public int parcoolskill$getSkillTime() {
            return parcoolskill$skillTime;
        }

        @Override
        public void parcoolskill$setSkillTime(int skillTime) {
            parcoolskill$skillTime = skillTime;
        }

        @Override
        public int parcoolskill$getCooldown() {
            return parcoolskill$cooldown;
        }

        @Override
        public void parcoolskill$setCooldown(int cooldown) {
            parcoolskill$cooldown = cooldown;
        }

        @Override
        public Type parcoolskill$getReadyType() {
            return parcoolskill$readyType;
        }

        @Override
        public void parcoolskill$setReadyType(Type type) {
            parcoolskill$readyType = type;
        }

        @Override
        public int parcoolskill$getInvulnerableTime() {
            return parcoolskill$invulnerableTime;
        }

        @Override
        public void parcoolskill$setInvulnerableTime(int invulnerableTime) {
            parcoolskill$invulnerableTime = invulnerableTime;
        }
    }
}
