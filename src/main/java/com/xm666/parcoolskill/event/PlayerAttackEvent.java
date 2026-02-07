package com.xm666.parcoolskill.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class PlayerAttackEvent extends PlayerEvent {
    private final LivingEntity target;
    private final CriticalHitEvent critEvent;
    private float amount;

    protected PlayerAttackEvent(Player player, LivingEntity target, CriticalHitEvent critEvent, float amount) {
        super(player);
        this.target = target;
        this.critEvent = critEvent;
        this.amount = amount;
    }

    public LivingEntity getTarget() {
        return target;
    }

    public CriticalHitEvent getCritEvent() {
        return critEvent;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public boolean isFullStrength() {
        var player = getEntity();
        var attackStrengthScale = player.getAttackStrengthScale(0.5F);
        return attackStrengthScale > 0.9F;
    }

    public static class Pre extends PlayerAttackEvent {
        public Pre(Player player, LivingEntity target, CriticalHitEvent critEvent, float amount) {
            super(player, target, critEvent, amount);
        }
    }

    public static class Post extends PlayerAttackEvent {
        public Post(Player player, LivingEntity target, CriticalHitEvent critEvent, float amount) {
            super(player, target, critEvent, amount);
        }
    }
}