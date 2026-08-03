package com.xm666.parcoolskill.mixin.parcoolskill.skill;

import com.alrex.parcool.common.action.impl.ChargeJump;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.xm666.parcoolskill.skill.JumpSkill;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashSet;

public class JumpSkillMixin {
    @Mixin(value = ChargeJump.class, remap = false)
    private static class ChargeJumpMixin implements JumpSkill {
        @Unique
        private final HashSet<Entity> parcoolskill$entityHits = new HashSet<>();
        @Shadow
        private int notChargeTick;
        @Unique
        private boolean parcoolskill$attackReady;
        @Unique
        private int parcoolskill$attackTime;
        @Unique
        private boolean parcoolskill$coolingDown;

        @Override
        public boolean parcoolskill$isAttackReady() {
            return parcoolskill$attackReady;
        }

        @Override
        public void parcoolskill$setAttackReady(boolean attackReady) {
            parcoolskill$attackReady = attackReady;
        }

        @Override
        public int parcoolskill$getAttackTime() {
            return parcoolskill$attackTime;
        }

        @Override
        public void parcoolskill$setAttackTime(int attackTime) {
            parcoolskill$attackTime = attackTime;
        }

        @Override
        public boolean parcoolskill$addEntityHit(Entity entity) {
            return parcoolskill$entityHits.add(entity);
        }

        @Override
        public void parcoolskill$clearEntityHits() {
            parcoolskill$entityHits.clear();
        }

        @Override
        public boolean parcoolskill$isCoolingDown() {
            return parcoolskill$coolingDown;
        }

        @Override
        public void parcoolskill$setCoolingDown(boolean coolingDown) {
            parcoolskill$coolingDown = coolingDown;
        }

        @Override
        public int getNotChargingTick() {
            return notChargeTick;
        }

        @OnlyIn(Dist.CLIENT)
        @Definition(id = "coolTimeTick", field = "Lcom/alrex/parcool/common/action/impl/ChargeJump;coolTimeTick:I")
        @Expression("this.coolTimeTick <= 0")
        @WrapOperation(method = "onClientTick", at = @At(value = "MIXINEXTRAS:EXPRESSION"))
        private boolean wrapCoolTimeCondition(int left, int right, Operation<Boolean> original) {
            return true;
        }

        @OnlyIn(Dist.CLIENT)
        @Definition(id = "keySneak", field = "Lcom/alrex/parcool/client/input/KeyRecorder;keySneak:Lcom/alrex/parcool/client/input/KeyRecorder$KeyState;")
        @Definition(id = "getPreviousTickNotKeyDown", method = "Lcom/alrex/parcool/client/input/KeyRecorder$KeyState;getPreviousTickNotKeyDown()I")
        @Expression("keySneak.getPreviousTickNotKeyDown() > 5")
        @WrapOperation(method = "onClientTick", at = @At(value = "MIXINEXTRAS:EXPRESSION"))
        private boolean wrapSneakCondition(int left, int right, Operation<Boolean> original) {
            return true;
        }
    }
}
