package com.xm666.parcoolskill.mixin.lrtactical;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.mojang.datafixers.util.Function3;
import com.xm666.parcoolskill.event.PlayerAttackEvent;
import me.xjqsh.lrtactical.api.melee.AttackResult;
import me.xjqsh.lrtactical.config.CommonConfig;
import me.xjqsh.lrtactical.item.MeleeItem;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

public class MeleeAttackMixin {
    @Mixin(MeleeItem.class)
    private static class MeleeWeaponMixin {
        @Unique
        public AttackResult performAttack(Player attacker, Entity target, ItemStack stack, float base, float knockback) {
            if (!ForgeHooks.onPlayerAttackTarget(attacker, target)) return AttackResult.MISS;
            if (!target.isAttackable()) return AttackResult.MISS;
            if (target.skipAttackInteraction(attacker)) return AttackResult.MISS;

            float modifier;
            if (target instanceof LivingEntity living) {
                modifier = EnchantmentHelper.getDamageBonus(stack, living.getMobType());
            } else {
                modifier = EnchantmentHelper.getDamageBonus(stack, MobType.UNDEFINED);
            }

            var flag2 = attacker.fallDistance > 0.0F && !attacker.onGround() && !attacker.onClimbable() && !attacker.isInWater()
                    && !attacker.hasEffect(MobEffects.BLINDNESS) && !attacker.isPassenger() && target instanceof LivingEntity;

            var disableCrit = new LocalBooleanRef() {
                private boolean value;

                public boolean get() {
                    return value;
                }

                public void set(boolean value) {
                    this.value = value;
                }
            };
            var disableSweep = new LocalBooleanRef() {
                private boolean value;

                public boolean get() {
                    return value;
                }

                public void set(boolean value) {
                    this.value = value;
                }
            };
            var hitResult = modifyCriticalHit(ForgeHooks.getCriticalHit(attacker, target, flag2, flag2 ? 1.5F : 1.0F), attacker, target, disableCrit, disableSweep);
            if (hitResult != null) {
                base *= hitResult.getDamageModifier();
                flag2 = true;
            }

            var j = EnchantmentHelper.getFireAspect(attacker);
            if (target instanceof LivingEntity living) {
                if (j > 0) {
                    living.setSecondsOnFire(j * 4);
                }
            }

            if (target.invulnerableTime < CommonConfig.MELEE_IGNORE_INVULNERABLE_TICK_THRESHOLD.get()) {
                target.invulnerableTime = 0;
            }

            var result = onHurt(target, attacker.damageSources().playerAttack(attacker), base + modifier, Entity::hurt, attacker, hitResult, disableCrit);
            if (!result) {
                return AttackResult.MISS;
            }

            if (target instanceof LivingEntity living) {
                living.knockback(knockback, Mth.sin(attacker.getYRot() * ((float) Math.PI / 180F)), -Mth.cos(attacker.getYRot() * ((float) Math.PI / 180F)));
                EnchantmentHelper.doPostHurtEffects(living, attacker);
            }

            EnchantmentHelper.doPostDamageEffects(attacker, target);

            if (flag2 && wrapCrit(attacker, target, disableCrit)) {
                attacker.crit(target);
            }

            var killed = target instanceof LivingEntity living && living.isDeadOrDying();

            if (killed) {
                return flag2 ? AttackResult.CRIT_KILL : AttackResult.KILL;
            }

            return flag2 ? AttackResult.CRIT : AttackResult.HIT;
        }

        @Unique
        private CriticalHitEvent modifyCriticalHit(CriticalHitEvent critEvent, Player player, Entity target, @Share("disableCrit") LocalBooleanRef disableCrit, @Share("disableSweep") LocalBooleanRef disableSweep) {
            if (critEvent == null) {
                critEvent = new CriticalHitEvent(player, target, 1.0F, false);
            }
            var playerAttackEvent = new PlayerAttackEvent.Pre(critEvent);
            MinecraftForge.EVENT_BUS.post(playerAttackEvent);
            if (!playerAttackEvent.isCriticalHit()) return null;

            critEvent.setDamageModifier(playerAttackEvent.getDamageMultiplier());
            try {
                var field = CriticalHitEvent.class.getDeclaredField("vanillaCritical");
                field.setAccessible(true);
                field.set(critEvent, true);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            disableCrit.set(playerAttackEvent.disableCrit());
            disableSweep.set(playerAttackEvent.disableSweep());
            return critEvent;
        }

        @Unique
        private boolean onHurt(Entity instance, DamageSource source, float amount, Function3<Entity, DamageSource, Float, Boolean> original, Player player, @Local(name = "hitResult") CriticalHitEvent critEvent, @Share("disableCrit") LocalBooleanRef disableCrit) {
            var flag = original.apply(instance, source, amount);
            if (critEvent == null) {
                critEvent = new CriticalHitEvent(player, instance, 1.0F, false);
            }
            var playerAttackEvent = new PlayerAttackEvent.Post(critEvent, source);
            playerAttackEvent.setDisableCrit(disableCrit.get());
            MinecraftForge.EVENT_BUS.post(playerAttackEvent);

            disableCrit.set(playerAttackEvent.disableCrit());
            return flag;
        }

        @Unique
        private boolean wrapCrit(Player instance, Entity entityHit, @Share("disableCrit") LocalBooleanRef disableCrit) {
            return !disableCrit.get();
        }
    }
}
