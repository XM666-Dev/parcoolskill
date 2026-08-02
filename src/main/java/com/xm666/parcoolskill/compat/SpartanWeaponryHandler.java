package com.xm666.parcoolskill.compat;

import com.oblivioussp.spartanweaponry.api.WeaponTraits;
import com.oblivioussp.spartanweaponry.item.HeavyCrossbowItem;
import com.oblivioussp.spartanweaponry.item.LongbowItem;
import com.oblivioussp.spartanweaponry.item.SwordBaseItem;
import com.oblivioussp.spartanweaponry.item.ThrowingWeaponItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class SpartanWeaponryHandler {
    public static Optional<Float> getPower(ItemStack stack, LivingEntity shooter) {
        var item = stack.getItem();
        var charge = stack.getUseDuration() - shooter.getUseItemRemainingTicks();
        if (item instanceof SwordBaseItem swordBaseItem) {
            return getPowerForTime(swordBaseItem, charge);
        } else if (item instanceof LongbowItem longbowItem) {
            return Optional.of(longbowItem.getNockProgress(stack, shooter));
        } else if (item instanceof HeavyCrossbowItem heavyCrossbowItem) {
            return Optional.of(stack.getTag().getBoolean(HeavyCrossbowItem.NBT_CHARGED)
                    ? (float) charge / heavyCrossbowItem.getAimTicks(stack)
                    : heavyCrossbowItem.getLoadProgress(stack, shooter));
        } else if (item instanceof ThrowingWeaponItem throwingWeaponItem) {
            return Optional.of(getPowerForTime(throwingWeaponItem, charge, stack, shooter));
        }
        return Optional.empty();
    }

    private static Optional<Float> getPowerForTime(SwordBaseItem swordBaseItem, int charge) {
        return Optional.ofNullable(swordBaseItem.hasWeaponTraitWithType(WeaponTraits.TYPE_THROWABLE) ? charge / 5.0F : null);
    }

    private static float getPowerForTime(ThrowingWeaponItem throwingWeapon, int charge, ItemStack stack, LivingEntity shooter) {
        var maxCharge = throwingWeapon.getMaxChargeTicks(stack);
        var finalCharge = charge > 2 ? charge : Math.min(charge, maxCharge - 1);
        return (float) finalCharge / maxCharge;
    }
}
