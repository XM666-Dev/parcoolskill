package com.xm666.parcoolskill.mixin;

import com.alrex.parcool.common.action.impl.CatLeap;
import com.xm666.parcoolskill.handler.DropkickHandler;
import com.xm666.parcoolskill.handler.WildStrikeHandler;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CatLeap.class)
public class CatLeapMixin {
    public void onStop(Player player) {
        WildStrikeHandler.queueAttack = false;
        WildStrikeHandler.queueInvulnerable = false;
        DropkickHandler.queueInvulnerable = false;
    }
}
