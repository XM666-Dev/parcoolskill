package com.xm666.parcoolskill.mixin.parcoolskill.skill;

import com.alrex.parcool.common.action.impl.CatLeap;
import com.alrex.parcool.common.action.impl.ChargeJump;
import com.alrex.parcool.common.capability.Parkourability;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@OnlyIn(Dist.CLIENT)
public class BashMixin {
    @Mixin(value = ChargeJump.class, remap = false)
    private static class ChargeJumpMixin {
        @ModifyConstant(method = "onJump", constant = @Constant(doubleValue = 0.5))
        private double modifyJumpThreshold(double constant, Player player, Parkourability parkourability) {
            return parkourability.get(CatLeap.class).isDoing() ? constant : 1.0 / ChargeJump.JUMP_MAX_CHARGE_TICK;
        }
    }
}
