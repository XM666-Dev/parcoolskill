package com.xm666.parcoolskill.mixin.parcoolskill.skill;

import com.alrex.parcool.common.action.impl.CatLeap;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.xm666.parcoolskill.skill.LeapSkill;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

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
        @WrapOperation(method = "canStart", at = @At(value = "MIXINEXTRAS:EXPRESSION"))
        private boolean wrapCoolTimeCondition(int left, int right, Operation<Boolean> original) {
            return true;
        }
    }

    @Mixin(LivingEntity.class)
    private static class LivingEntityMixin {
        @ModifyExpressionValue(method = "applyItemBlocking", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getItemBlockingWith()Lnet/minecraft/world/item/ItemStack;"))
        private ItemStack modifyItemBlockingWith(ItemStack original) {
            if (original != null || !((Object) this instanceof Player player)) return original;

            var parkourability = Parkourability.get(player);
            var leapSkill = (LeapSkill) parkourability.get(CatLeap.class);
            if (leapSkill.parcoolskill$getParryTime() == 0) return null;

            return player.getWeaponItem();
        }

        @ModifyExpressionValue(method = "applyItemBlocking", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;"))
        private Object modifyBlocksAttacks(Object original) {
            if (original != null || !((Object) this instanceof Player player)) return original;

            var parkourability = Parkourability.get(player);
            var leapSkill = (LeapSkill) parkourability.get(CatLeap.class);
            if (leapSkill.parcoolskill$getParryTime() == 0) return null;

            return Items.SHIELD.components().get(DataComponents.BLOCKS_ATTACKS);
        }

        @ModifyExpressionValue(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;"))
        private Object wrapBlocksAttacks(Object original) {
            if (original != null || !((Object) this instanceof Player player)) return original;

            var parkourability = Parkourability.get(player);
            var leapSkill = (LeapSkill) parkourability.get(CatLeap.class);
            if (leapSkill.parcoolskill$getParryTime() == 0) return null;

            return Items.SHIELD.components().get(DataComponents.BLOCKS_ATTACKS);
        }
    }
}
