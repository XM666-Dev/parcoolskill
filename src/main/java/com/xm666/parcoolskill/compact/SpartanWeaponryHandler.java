package com.xm666.parcoolskill.compact;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.xiyu.spartanweaponryunofficial.api.WeaponTraits;
import org.xiyu.spartanweaponryunofficial.item.HeavyCrossbowItem;
import org.xiyu.spartanweaponryunofficial.item.LongbowItem;
import org.xiyu.spartanweaponryunofficial.item.SwordBaseItem;
import org.xiyu.spartanweaponryunofficial.item.ThrowingWeaponItem;

import java.util.Optional;

public class SpartanWeaponryHandler {
    public static Optional<Float> getPower(ItemStack stack, LivingEntity shooter) {
        var item = stack.getItem();
        var charge = stack.getUseDuration(shooter) - shooter.getUseItemRemainingTicks();
        return switch (item) {
            case SwordBaseItem swordBaseItem -> getPowerForTime(swordBaseItem, charge);
            default -> Optional.ofNullable(switch (item) {
                case LongbowItem longbowItem -> longbowItem.getNockProgress(stack, shooter);
                case HeavyCrossbowItem heavyCrossbowItem -> heavyCrossbowItem.getLoadProgress(stack, shooter);
                case ThrowingWeaponItem throwingWeaponItem ->
                        getPowerForTime(throwingWeaponItem, charge, stack, shooter);
                default -> null;
            });
        };
    }

    private static Optional<Float> getPowerForTime(SwordBaseItem swordBaseItem, int charge) {
        return Optional.ofNullable(swordBaseItem.hasWeaponTraitWithType(WeaponTraits.TYPE_THROWABLE) ? charge / 5.0F : null);
    }

    private static float getPowerForTime(ThrowingWeaponItem throwingWeapon, int charge, ItemStack stack, LivingEntity shooter) {
        var maxCharge = throwingWeapon.getMaxChargeTicks(stack, shooter.level());
        var finalCharge = charge > 2 ? charge : Math.min(charge, maxCharge - 1);
        return (float) finalCharge / maxCharge;
    }
}
