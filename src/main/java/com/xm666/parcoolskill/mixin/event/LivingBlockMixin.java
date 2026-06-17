package com.xm666.parcoolskill.mixin.event;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.xm666.parcoolskill.event.LivingBlockEvent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class LivingBlockMixin {
    @Mixin(LivingEntity.class)
    private abstract static class LivingEntityMixin {
        @Shadow
        public abstract boolean isBlocking();

        @Shadow
        public abstract ItemStack getWeaponItem();

        @Inject(method = "applyItemBlocking", at = @At("HEAD"))
        private void onApplyItemBlocking(ServerLevel level, DamageSource damageSource, float damageAmount, CallbackInfoReturnable<Float> cir, @Share("parry") LocalBooleanRef parry) {
            parry.set(NeoForge.EVENT_BUS.post(new LivingBlockEvent.Attack((LivingEntity) (Object) this, isBlocking())).isSuccessful());
        }

        @ModifyExpressionValue(method = "applyItemBlocking", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getItemBlockingWith()Lnet/minecraft/world/item/ItemStack;"))
        public ItemStack modifyItemBlockingWith(ItemStack original, @Share("parry") LocalBooleanRef parry) {
            if (original != null) return original;

            return parry.get() ? getWeaponItem() : null;
        }

        @ModifyExpressionValue(method = "applyItemBlocking", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;"))
        public Object modifyAttackComponent(Object original, @Share("parry") LocalBooleanRef parry) {
            if (original != null) return original;

            return parry.get() ? Items.SHIELD.components().get(DataComponents.BLOCKS_ATTACKS) : null;
        }

        @ModifyExpressionValue(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;"))
        public Object modifySoundComponent(Object original) {
            if (original != null) return original;

            var parry = NeoForge.EVENT_BUS.post(new LivingBlockEvent.Sound((LivingEntity) (Object) this, isBlocking())).isSuccessful();
            return parry ? Items.SHIELD.components().get(DataComponents.BLOCKS_ATTACKS) : null;
        }
    }
}
