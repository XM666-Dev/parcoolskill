package com.xm666.parcoolskill.event;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class LivingBlockEvent extends LivingEvent {
    private boolean isSuccess;

    public LivingBlockEvent(LivingEntity entity, boolean success) {
        super(entity);
        this.isSuccess = success;
    }

    public boolean isSuccessful() {
        return this.isSuccess;
    }

    public void setSuccessful(boolean success) {
        this.isSuccess = success;
    }
}
