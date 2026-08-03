package com.xm666.parcoolskill.mixin.parcoolskill.skill;

import com.alrex.parcool.common.action.impl.Flipping;
import com.xm666.parcoolskill.skill.FlippingSkill;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

public class FlippingSkillMixin {
    @Mixin(value = Flipping.class, remap = false)
    private static class FlippingMixin implements FlippingSkill {
        @Shadow
        private boolean justJumped;
        @Unique
        private int parcoolskill$skillTime;
        @Unique
        private int parcoolskill$cooldown;
        @Unique
        private boolean parcoolskill$attackReady;
        @Unique
        private boolean parcoolskill$accelerated;
        @Unique
        private int parcoolskill$parryTime;

        @Override
        public boolean parcoolskill$justJumped() {
            return justJumped;
        }

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
        public boolean parcoolskill$isAttackReady() {
            return parcoolskill$attackReady;
        }

        @Override
        public void parcoolskill$setAttackReady(boolean attackReady) {
            parcoolskill$attackReady = attackReady;
        }

        @Override
        public boolean parcoolskill$isAccelerated() {
            return parcoolskill$accelerated;
        }

        @Override
        public void parcoolskill$setAccelerated(boolean accelerated) {
            parcoolskill$accelerated = accelerated;
        }

        @Override
        public int parcoolskill$getParryTime() {
            return parcoolskill$parryTime;
        }

        @Override
        public void parcoolskill$setParryTime(int parryTime) {
            parcoolskill$parryTime = parryTime;
        }
    }
}
