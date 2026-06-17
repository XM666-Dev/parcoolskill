package com.xm666.parcoolskill.event;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class LivingBlockEvent extends LivingEvent {
    private boolean isSuccess;

    private LivingBlockEvent(LivingEntity entity, boolean success) {
        super(entity);
        this.isSuccess = success;
    }

    public boolean isSuccessful() {
        return this.isSuccess;
    }

    public void setSuccessful(boolean success) {
        this.isSuccess = success;
    }

    public static class Attack extends LivingBlockEvent {
        public Attack(LivingEntity entity, boolean success) {
            super(entity, success);
        }
    }

    public static class Sound extends LivingBlockEvent {
        public Sound(LivingEntity entity, boolean success) {
            super(entity, success);
        }
    }
}
