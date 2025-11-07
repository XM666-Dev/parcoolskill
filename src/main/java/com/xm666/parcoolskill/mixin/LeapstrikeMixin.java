package com.xm666.parcoolskill.mixin;

import com.alrex.parcool.common.action.impl.CatLeap;
import com.xm666.parcoolskill.action.LeapstrikeLeap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

public class LeapstrikeMixin {
    @Mixin(CatLeap.class)
    private static class CatLeapMixin implements LeapstrikeLeap {
        @Unique
        private boolean parcoolskill$queueAttack;
        @Unique
        private boolean parcoolskill$queueInvulnerable;

        @Override
        public boolean parcoolskill$isQueueAttack() {
            return parcoolskill$queueAttack;
        }

        @Override
        public void parcoolskill$setQueueAttack(boolean queue) {
            parcoolskill$queueAttack = queue;
        }

        @Override
        public boolean parcoolskill$isQueueInvulnerable() {
            return parcoolskill$queueInvulnerable;
        }

        @Override
        public void parcoolskill$setQueueInvulnerable(boolean queue) {
            parcoolskill$queueInvulnerable = queue;
        }
    }
}
