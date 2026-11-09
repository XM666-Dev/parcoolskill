package com.xm666.parcoolskill.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class StaminaClientHandler {
    public static Player getPlayer() {
        var mc = Minecraft.getInstance();
        return mc.player;
    }
}
