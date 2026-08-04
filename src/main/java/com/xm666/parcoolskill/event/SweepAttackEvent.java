package com.xm666.parcoolskill.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class SweepAttackEvent extends PlayerEvent {
    private final Entity target;
    private final boolean isVanillaSweep;

    private boolean isSweeping;

    public SweepAttackEvent(Player player, Entity target, boolean isVanillaSweep) {
        super(player);
        this.target = target;
        this.isSweeping = this.isVanillaSweep = isVanillaSweep;
    }

    public Entity getTarget() {
        return this.target;
    }

    public boolean isVanillaSweep() {
        return this.isVanillaSweep;
    }

    public boolean isSweeping() {
        return this.isSweeping;
    }

    public void setSweeping(boolean sweep) {
        this.isSweeping = sweep;
    }
}
