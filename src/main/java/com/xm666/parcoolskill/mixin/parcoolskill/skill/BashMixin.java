package com.xm666.parcoolskill.mixin.parcoolskill.skill;

import com.alrex.parcool.common.action.impl.CatLeap;
import com.alrex.parcool.common.action.impl.ChargeJump;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.xm666.parcoolskill.skill.JumpSkill;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.ByteBuffer;

@OnlyIn(Dist.CLIENT)
public class BashMixin {
    @Mixin(ChargeJump.class)
    private static class ChargeJumpMixin {
        @Shadow
        @Final
        public static int JUMP_MAX_CHARGE_TICK;

        @Shadow
        private int lastChargeTick;

        @ModifyConstant(method = "onJump", constant = @Constant(doubleValue = 0.5))
        private double modifyJumpThreshold(double constant, Player player, Parkourability parkourability) {
            return parkourability.get(CatLeap.class).isDoing() ? constant : 1.0 / ChargeJump.JUMP_MAX_CHARGE_TICK;
        }

        @Inject(method = "canStart", at = @At("TAIL"))
        public void onCanStart(Player player, Parkourability parkourability, ByteBuffer startInfo, CallbackInfoReturnable<Boolean> cir) {
            startInfo.putInt(lastChargeTick == JUMP_MAX_CHARGE_TICK ? 1 : 0);
        }

        @Unique
        public void onStartInServer(Player player, Parkourability parkourability, ByteBuffer startInfo) {
            try {
                if (startInfo.getInt() == 0) return;
            } catch (Exception e) {
                return;
            }

            var jumpSkill = (JumpSkill) this;
            jumpSkill.parcoolskill$setFullCharged(true);
        }
    }
}
