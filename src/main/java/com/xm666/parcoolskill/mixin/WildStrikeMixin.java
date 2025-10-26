package com.xm666.parcoolskill.mixin;

import com.alrex.parcool.common.action.impl.CatLeap;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.xm666.parcoolskill.handler.WildStrikeHandler;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;

import java.nio.ByteBuffer;

public class WildStrikeMixin {
    @Mixin(CatLeap.class)
    private static class CatLeapMixin {
        public void onStart(Player player, Parkourability parkourability, ByteBuffer startData) {
            WildStrikeHandler.queueAttack = true;
        }
    }
}
