package com.xm666.parcoolskill.mixin.event;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.xm666.parcoolskill.event.LivingBlockEvent;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlocksAttacks;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

public class LivingBlockMixin {
    @Mixin(LivingEntity.class)
    private static class LivingEntityMixin {
        @ModifyExpressionValue(method = "applyItemBlocking", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getItemBlockingWith()Lnet/minecraft/world/item/ItemStack;"))
        private ItemStack modifyItemBlockingWith(ItemStack original, @Share("blocksAttacks") LocalRef<BlocksAttacks> blocksAttacksRef) {
            var living = (LivingEntity) (Object) this;
            var blocksAttacks = original != null ? original.get(DataComponents.BLOCKS_ATTACKS) : null;
            var event = new LivingBlockEvent(living, original, blocksAttacks);
            NeoForge.EVENT_BUS.post(event);
            blocksAttacksRef.set(event.getBlocksAttacks());
            return event.getItemBlockingWith();
        }

        @Redirect(method = "applyItemBlocking", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;"))
        private Object redirectBlocksAttacks(ItemStack instance, DataComponentType<?> dataComponentType, @Share("blocksAttacks") LocalRef<BlocksAttacks> blocksAttacksRef) {
            return blocksAttacksRef.get();
        }

        @WrapOperation(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;"))
        private Object wrapBlocksAttacks(ItemStack instance, DataComponentType<?> dataComponentType, Operation<Object> original) {
            var living = (LivingEntity) (Object) this;
            var blocksAttacks = (BlocksAttacks) original.call(instance, dataComponentType);
            var event = new LivingBlockEvent(living, instance, blocksAttacks);
            NeoForge.EVENT_BUS.post(event);
            return event.getBlocksAttacks();
        }
    }
}
