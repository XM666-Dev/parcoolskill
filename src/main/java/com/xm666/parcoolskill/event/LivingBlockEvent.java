package com.xm666.parcoolskill.event;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class LivingBlockEvent extends LivingEvent {
    private boolean blocking;

    public LivingBlockEvent(LivingEntity entity, boolean blocking) {
        super(entity);
        this.blocking = blocking;
    }

    public boolean isBlocking() {
        return this.blocking;
    }

    public void setBlocking(boolean blocking) {
        this.blocking = blocking;
    }
}
