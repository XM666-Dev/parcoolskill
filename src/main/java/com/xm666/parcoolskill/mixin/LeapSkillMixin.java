package com.xm666.parcoolskill.mixin;

import com.alrex.parcool.common.action.impl.CatLeap;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.xm666.parcoolskill.skill.LeapSkill;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class LeapSkillMixin {
    @Mixin(CatLeap.class)
    private static class CatLeapMixin implements LeapSkill {
        @Unique
        private boolean parcoolskill$attackReady;
        @Unique
        private int parcoolskill$parryTime;

        @Override
        public boolean parcoolskill$isAttackReady() {
            return parcoolskill$attackReady;
        }

        @Override
        public void parcoolskill$setAttackReady(boolean attackReady) {
            parcoolskill$attackReady = attackReady;
        }

        @Override
        public int parcoolskill$getParryTime() {
            return parcoolskill$parryTime;
        }

        @Override
        public void parcoolskill$setParryTime(int parryTime) {
            parcoolskill$parryTime = parryTime;
        }

        @OnlyIn(Dist.CLIENT)
        @Definition(id = "coolTimeTick", field = "Lcom/alrex/parcool/common/action/impl/CatLeap;coolTimeTick:I")
        @Expression("this.coolTimeTick <= 0")
        @WrapOperation(method = "canStart", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0))
        private boolean wrapCoolTimeCondition(int left, int right, Operation<Boolean> original) {
            return true;
        }
    }

    @Mixin(LivingEntity.class)
    private static abstract class LivingEntityMixin {
        @Shadow
        public abstract ItemStack getWeaponItem();

        @SuppressWarnings("ConstantValue")
        @Inject(method = "applyItemBlocking", at = @At("HEAD"))
        private void onApplyItemBlocking(ServerLevel level, DamageSource damageSource, float damageAmount, CallbackInfoReturnable<Float> cir, @Share("parry") LocalBooleanRef parry) {
            if (!((Object) this instanceof Player player)) return;

            var leapSkill = (LeapSkill) Parkourability.get(player).get(CatLeap.class);
            parry.set(leapSkill.parcoolskill$getParryTime() > 0);
        }

        @ModifyExpressionValue(method = "applyItemBlocking", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getItemBlockingWith()Lnet/minecraft/world/item/ItemStack;"))
        public ItemStack modifyItemBlockingWith(ItemStack original, @Share("parry") LocalBooleanRef parry) {
            if (original != null) return original;

            return parry.get() ? getWeaponItem() : null;
        }

        @ModifyExpressionValue(method = "applyItemBlocking", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;"))
        public Object modifyComponent(Object original, @Share("parry") LocalBooleanRef parry) {
            if (original != null) return original;

            return parry.get() ? Items.SHIELD.components().get(DataComponents.BLOCKS_ATTACKS) : null;
        }
    }
}
