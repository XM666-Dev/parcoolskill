package com.xm666.parcoolskill.mixin.event;

import com.xm666.parcoolskill.event.LivingBlockEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;

public class LivingBlockMixin {
    @Mixin(Player.class)
    private abstract static class PlayerMixin extends LivingEntity {
        protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
            super(entityType, level);
        }

        public boolean isBlocking() {
            return NeoForge.EVENT_BUS.post(new LivingBlockEvent(this, super.isBlocking())).isSuccessful();
        }
    }
}
