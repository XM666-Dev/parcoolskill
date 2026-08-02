package com.xm666.parcoolskill.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class PlayerAttackEvent extends PlayerEvent {
    private final Entity target;
    private final float vanillaDamageMultiplier;
    private final boolean isVanillaCritical;
    private float damageMultiplier;
    private boolean isCriticalHit;
    private boolean disableSweep;
    private boolean disableCrit;

    private PlayerAttackEvent(CriticalHitEvent critEvent) {
        this(critEvent.getEntity(), critEvent.getTarget(), critEvent.getOldDamageModifier(), critEvent.isVanillaCritical());
        this.damageMultiplier = critEvent.getDamageModifier();
        this.isCriticalHit = critEvent.isVanillaCritical();
        this.disableSweep = true;
    }

    private PlayerAttackEvent(Player player, Entity target, float damageMultiplier, boolean isCriticalHit) {
        super(player);
        this.target = target;
        this.damageMultiplier = this.vanillaDamageMultiplier = damageMultiplier;
        this.isCriticalHit = this.isVanillaCritical = isCriticalHit;
    }

    public Entity getTarget() {
        return target;
    }

    public float getDamageMultiplier() {
        return this.damageMultiplier;
    }

    public void setDamageMultiplier(float damageMultiplier) {
        this.damageMultiplier = damageMultiplier;
    }

    public boolean isCriticalHit() {
        return this.isCriticalHit;
    }

    public void setCriticalHit(boolean isCriticalHit) {
        this.isCriticalHit = isCriticalHit;
    }

    public float getVanillaMultiplier() {
        return this.vanillaDamageMultiplier;
    }

    public boolean isVanillaCritical() {
        return this.isVanillaCritical;
    }

    public void setDisableSweep(boolean disableSweep) {
        this.disableSweep = disableSweep;
    }

    public boolean disableSweep() {
        return this.disableSweep;
    }

    public void setDisableCrit(boolean disableCrit) {
        this.disableCrit = disableCrit;
    }

    public boolean disableCrit() {
        return this.disableCrit;
    }

    public boolean isFullStrength() {
        var player = getEntity();
        var attackStrengthScale = player.getAttackStrengthScale(0.5F);
        return attackStrengthScale > 0.9F;
    }

    public static class Pre extends PlayerAttackEvent {
        public Pre(CriticalHitEvent critEvent) {
            super(critEvent);
        }
    }

    public static class Post extends PlayerAttackEvent {
        public Post(CriticalHitEvent critEvent) {
            super(critEvent);
        }
    }
}
