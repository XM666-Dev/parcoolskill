package com.xm666.parcoolskill.event;

import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class DamageBlockEvent extends LivingEvent {
    private final DamageContainer container;
    private final float originalDmgBlocked;
    private final boolean originalBlocked;
    private float dmgBlocked;
    private int shieldDamage = -1;
    private boolean newBlocked;

    public DamageBlockEvent(LivingEntity blocker, DamageContainer container, float blockedDamage, boolean originalBlockedState) {
        super(blocker);
        this.container = container;
        this.dmgBlocked = blockedDamage;
        this.originalDmgBlocked = this.dmgBlocked;
        this.originalBlocked = originalBlockedState;
        this.newBlocked = originalBlockedState;
    }

    public DamageContainer getDamageContainer() {
        return this.container;
    }

    public DamageSource getDamageSource() {
        return this.getDamageContainer().getSource();
    }

    public float getOriginalBlockedDamage() {
        return this.originalDmgBlocked;
    }

    public float getBlockedDamage() {
        return Math.min(this.dmgBlocked, this.container.getNewDamage());
    }

    public void setBlockedDamage(float blocked) {
        this.dmgBlocked = Mth.clamp(blocked, 0.0F, this.container.getNewDamage());
    }

    public int shieldDamage() {
        return this.newBlocked ? this.shieldDamage : 0;
    }

    public void setShieldDamage(int damage) {
        this.shieldDamage = damage;
    }

    public boolean getOriginalBlock() {
        return this.originalBlocked;
    }

    public boolean getBlocked() {
        return this.newBlocked;
    }

    public void setBlocked(boolean isBlocked) {
        this.newBlocked = isBlocked;
    }
}
