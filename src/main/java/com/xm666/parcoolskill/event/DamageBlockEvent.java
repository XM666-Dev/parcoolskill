package com.xm666.parcoolskill.event;

import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class DamageBlockEvent extends LivingEvent implements ICancellableEvent {
    private final DamageContainer container;
    private final boolean originalBlocked;
    private float dmgBlocked;
    private float shieldDamage;
    private boolean newBlocked;

    public DamageBlockEvent(LivingEntity blocker, DamageContainer container, boolean originalBlockedState) {
        super(blocker);
        this.container = container;
        this.dmgBlocked = container.getNewDamage();
        this.originalBlocked = originalBlockedState;
        this.newBlocked = originalBlockedState;
        this.shieldDamage = container.getNewDamage();
    }

    public DamageContainer getDamageContainer() {
        return this.container;
    }

    public DamageSource getDamageSource() {
        return this.getDamageContainer().getSource();
    }

    public float getOriginalBlockedDamage() {
        return this.getDamageContainer().getNewDamage();
    }

    public float getBlockedDamage() {
        return Math.min(this.dmgBlocked, container.getNewDamage());
    }

    public void setBlockedDamage(float blocked) {
        this.dmgBlocked = Mth.clamp(blocked, 0, this.getOriginalBlockedDamage());
    }

    public float shieldDamage() {
        if (newBlocked)
            return shieldDamage >= 0 ? shieldDamage : getBlockedDamage();
        return 0;
    }

    public void setShieldDamage(float damage) {
        this.shieldDamage = damage;
    }

    public boolean getOriginalBlock() {
        return originalBlocked;
    }

    public boolean getBlocked() {
        return newBlocked;
    }

    public void setBlocked(boolean isBlocked) {
        this.newBlocked = isBlocked;
    }
}
