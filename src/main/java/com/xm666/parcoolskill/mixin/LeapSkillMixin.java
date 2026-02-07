package com.xm666.parcoolskill.mixin;

import com.alrex.parcool.common.action.impl.CatLeap;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.xm666.parcoolskill.skill.LeapSkill;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

public class LeapSkillMixin {
    @Mixin(CatLeap.class)
    private static class CatLeapMixin implements LeapSkill {
        @Unique
        private boolean parcoolskill$attackReady;
        @Unique
        private int parcoolskill$parryTime;

        @Override
        public boolean parcoolskill$isAttackReady() {
            return parcoolskill$attackReady;
        }

        @Override
        public void parcoolskill$setAttackReady(boolean attackReady) {
            parcoolskill$attackReady = attackReady;
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

    @Mixin(Player.class)
    private abstract static class PlayerMixin extends LivingEntity {
        protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
            super(entityType, level);
        }

        public boolean isBlocking() {
            if (super.isBlocking()) return true;

            var player = (Player) (Object) this;
            var skillLeap = (LeapSkill) Parkourability.get(player).get(CatLeap.class);
            return skillLeap.parcoolskill$getParryTime() > 0;
        }
    }
}
