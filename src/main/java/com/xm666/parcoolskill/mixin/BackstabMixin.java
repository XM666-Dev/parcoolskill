package com.xm666.parcoolskill.mixin;

import com.alrex.parcool.common.action.impl.Dodge;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.xm666.parcoolskill.handler.BackstabHandler;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;

import java.nio.ByteBuffer;

public class BackstabMixin {
    @Mixin(Dodge.class)
    public static class DodgeMixin {
        public void onStart(Player player, Parkourability parkourability, ByteBuffer startData) {
            BackstabHandler.queueAttack = true;
        }

        public void onStop(Player player) {
            BackstabHandler.queueAttack = false;
        }
    }
}
