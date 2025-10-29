package com.xm666.parcoolskill.handler;

import com.xm666.parcoolskill.Config;
import com.xm666.parcoolskill.damage.DamageTypes;
import com.xm666.parcoolskill.network.KickPayload;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class KickHandler {
    public static void handlePayload(final KickPayload payload, final IPayloadContext context) {
        var level = context.player().level();
        var target = level.getEntity(payload.targetId());
        var source = level.getEntity(payload.sourceId());
        var type = KickPayload.Type.values()[payload.kickType()];
        if (target != null && source instanceof Player player) {
            var amount = Config.KICK_BASE_DAMAGE.get().floatValue();
            var baseArmor = player.getAttributeBaseValue(Attributes.ARMOR);
            var baseToughness = player.getAttributeBaseValue(Attributes.ARMOR_TOUGHNESS);
            amount += (float) baseArmor + (float) baseToughness;

            var feet = player.getItemBySlot(EquipmentSlot.FEET);
            amount += (float) getAttributeValue(feet, EquipmentSlot.FEET, Attributes.ARMOR, baseArmor);
            amount += (float) getAttributeValue(feet, EquipmentSlot.FEET, Attributes.ARMOR_TOUGHNESS, baseToughness);

            var legs = player.getItemBySlot(EquipmentSlot.LEGS);
            amount += (float) getAttributeValue(legs, EquipmentSlot.LEGS, Attributes.ARMOR, baseArmor);
            amount += (float) getAttributeValue(legs, EquipmentSlot.LEGS, Attributes.ARMOR_TOUGHNESS, baseToughness);

            DamageSource damageSource = new DamageSource(
                    level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.KICK_ATTACK),
                    player,
                    player,
                    player.position()
            );
            target.hurt(damageSource, amount);

            if (target instanceof LivingEntity livingTarget) {
                switch (type) {
                    case DROPKICK -> {
                        var strength = Config.DROPKICK_BASE_KNOCKBACK.get().floatValue();
                        strength += (float) player.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
                        livingTarget.knockback(strength * 0.5F, Mth.sin(player.getYRot() * ((float) Math.PI / 180F)), -Mth.cos(player.getYRot() * ((float) Math.PI / 180F)));
                    }
                    case SLIDEKICK -> {
                        var duration = 60;
                        var targetEffect = livingTarget.getEffect(MobEffects.MOVEMENT_SLOWDOWN);
                        duration += targetEffect != null ? targetEffect.getDuration() : 0;
                        livingTarget.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, 1, false, false), player);
                    }
                }
            }

            var sound = switch (type) {
                case DROPKICK -> SoundEvents.PLAYER_ATTACK_KNOCKBACK;
                case SLIDEKICK -> SoundEvents.PLAYER_ATTACK_STRONG;
            };
            level.playSound(null, source.getX(), source.getY(), source.getZ(), sound, source.getSoundSource(), 1.0F, 1.0F);
        }
    }

    private static double getAttributeValue(ItemStack stack, EquipmentSlot slot, Holder<Attribute> attribute, double baseValue) {
        var value = baseValue;
        var modifiers = stack.getAttributeModifiers().modifiers();
        for (var entry : modifiers) {
            if (entry.slot().test(slot) && entry.attribute().equals(attribute)) {
                var amount = entry.modifier().amount();
                var addition = switch (entry.modifier().operation()) {
                    case ADD_VALUE -> amount;
                    case ADD_MULTIPLIED_BASE -> amount * baseValue;
                    case ADD_MULTIPLIED_TOTAL -> amount * value;
                };
                value += addition;
            }
        }
        value -= baseValue;
        return value;
    }
}
