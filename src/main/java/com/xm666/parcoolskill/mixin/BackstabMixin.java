package com.xm666.parcoolskill.mixin;

import com.alrex.parcool.common.action.impl.Dodge;
import com.xm666.parcoolskill.action.BackstabDodge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

public class BackstabMixin {
    @Mixin(Dodge.class)
    public static class DodgeMixin implements BackstabDodge {
        @Unique
        private boolean parcoolskill$queueAttack;

        @Override
        public boolean parcoolskill$isQueueAttack() {
            return parcoolskill$queueAttack;
        }

        @Override
        public void parcoolskill$setQueueAttack(boolean queue) {
            parcoolskill$queueAttack = queue;
        }
    }
}
