package com.xm666.parcoolskill.handler;

import com.xm666.parcoolskill.ParCoolSkill;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber(modid = ParCoolSkill.MODID)
public class CleaveHandler {
    public static boolean queueAttack;

    //@SubscribeEvent
    //static void onSweepAttack(SweepAttackEvent event) {
    //    var entity = event.getEntity();
    //    var target = event.getTarget();
    //    if (event.isSweeping() && event.isVanillaSweep() && !entity.isLocalPlayer()) {
    //        var jump = (SkillJump) Parkourability.get(entity).get(ChargeJump.class);
    //        if (jump.parcoolskill$getChargeTick() < ChargeJump.JUMP_ANIMATION_TICK) return;
    //        queueAttack=true;
    //        event.setSweeping(false);
    //        PacketDistributor.sendToPlayer((ServerPlayer) entity, StopChargePayload.INSTANCE);
    //    }
    //}

    @SubscribeEvent
    static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        if (queueAttack) {
            var source = (Player) event.getSource().getEntity();
            if (source == null) return;
            event.setAmount(getCleaveDamage(source, event.getAmount()));
            queueAttack = false;

            //var target = event.getEntity();

            //float f = source.isAutoSpinAttack() ? source.autoSpinAttackDmg : (float) source.getAttributeValue(Attributes.ATTACK_DAMAGE);
            //ItemStack itemstack = source.getWeaponItem();
            //DamageSource damagesource = source.damageSources().playerAttack(source);
            //float f2 = source.getAttackStrengthScale(0.5F);
            ////float f7 = 1.0F + (float) player.getAttributeValue(Attributes.SWEEPING_DAMAGE_RATIO) * f;
            //float f7 = 1.0F + ((float) source.getAttributeValue(Attributes.SWEEPING_DAMAGE_RATIO) + 1.25F) * f;
            //
            ////for (LivingEntity livingentity : player.level().getEntitiesOfClass(LivingEntity.class, itemstack.getSweepHitBox(player, target))) {
            //var hitbox = itemstack.getSweepHitBox(source, target);
            //var scale = 2.0;
            //var multiplier = scale * 0.5 - 0.5;
            //var width = (hitbox.maxX - hitbox.minX) * multiplier;
            //var height = (hitbox.maxY - hitbox.minY) * multiplier;
            //var depth = (hitbox.maxZ - hitbox.minZ) * multiplier;
            //hitbox = new AABB(hitbox.minX - width, hitbox.minY - height, hitbox.minZ - depth, hitbox.maxX + width, hitbox.maxY + height, hitbox.maxZ + depth);
            //for (LivingEntity livingentity : source.level().getEntitiesOfClass(LivingEntity.class, hitbox)) {
            //    //double entityReachSq = Mth.square(player.entityInteractionRange());
            //    double entityReachSq = Mth.square(source.entityInteractionRange()) * scale;
            //    if (livingentity != source && livingentity != target && !source.isAlliedTo(livingentity) && (!(livingentity instanceof ArmorStand) || !((ArmorStand) livingentity).isMarker()) && source.distanceToSqr(livingentity) < entityReachSq) {
            //        float f5 = ((ServerPlayer) source).getEnchantedDamage(livingentity, f7, damagesource) * f2;
            //        livingentity.knockback(0.4F, Mth.sin(source.getYRot() * ((float) Math.PI / 180F)), -Mth.cos(source.getYRot() * ((float) Math.PI / 180F)));
            //        livingentity.hurt(damagesource, f5);
            //        Level var28 = source.level();
            //        if (var28 instanceof ServerLevel serverlevel) {
            //            EnchantmentHelper.doPostAttackEffects(serverlevel, livingentity, damagesource);
            //        }
            //    }
            //}
            //
            //source.level().playSound(null, source.getX(), source.getY(), source.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, source.getSoundSource(), 1.0F, 1.0F);
            //source.sweepAttack();
            //
            //event.setAmount(f7);
            //queueAttack=false;
        }
    }

    public static float getCleaveDamage(LivingEntity source, float amount) {
        return 1.0F + ((float) source.getAttributeValue(Attributes.SWEEPING_DAMAGE_RATIO) + 1.25F) * amount;
    }

    public static double getCleaveScale() {
        return 2.0;
    }
}
