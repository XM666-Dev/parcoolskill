package com.xm666.parcoolskill.handler;

import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import com.xm666.parcoolskill.Config;
import com.xm666.parcoolskill.ParCoolSkill;
import com.xm666.parcoolskill.action.SkillSlide;
import com.xm666.parcoolskill.damage.DamageTypes;
import com.xm666.parcoolskill.effect.Effects;
import com.xm666.parcoolskill.network.KickPayload;
import com.xm666.parcoolskill.network.StaminaRecoverPayload;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Arrays;
import java.util.function.Predicate;

@EventBusSubscriber(modid = ParCoolSkill.MODID)
public class KickHandler {
    @SubscribeEvent
    static void onSlideStart(ParCoolActionEvent.StartEvent event) {
        if (!(event.getAction() instanceof SkillSlide slide)) return;
        slide.parcoolskill$setQueueInvulnerable(false);
        DropkickHandler.onSlideStart(event);
        SlidekickHandler.onSlideStart(event);
    }

    public static void handlePayload(final KickPayload payload, final IPayloadContext context) {
        var level = context.player().level();
        var target = level.getEntity(payload.targetId());
        var source = level.getEntity(payload.sourceId());
        var type = KickPayload.Type.values()[payload.kickType()];
        if (target != null && source instanceof Player player) {
            var amount = (float) (Config.KICK_BASE_DAMAGE.get() +
                    calculateValue(player, Attributes.ATTACK_DAMAGE, m -> !m.is(ResourceLocation.parse("minecraft:base_attack_damage"))) +
                    calculateValue(player, Attributes.ARMOR, m -> m.is(ResourceLocation.parse("minecraft:armor.leggings")) || m.is(ResourceLocation.parse("minecraft:armor.boots"))) +
                    calculateValue(player, Attributes.ARMOR_TOUGHNESS, m -> m.is(ResourceLocation.parse("minecraft:armor.leggings")) || m.is(ResourceLocation.parse("minecraft:armor.boots"))));

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

                        if (livingTarget.hasEffect(Effects.VULNERABLE)) {
                            PacketDistributor.sendToPlayer((ServerPlayer) player, new StaminaRecoverPayload(200));
                            BulletTimeHandler.addScale(0.5F, 60);
                        }
                    }
                    case SLIDEKICK -> {
                        var duration = 60;
                        var targetEffect = livingTarget.getEffect(MobEffects.MOVEMENT_SLOWDOWN);
                        duration += targetEffect != null ? targetEffect.getDuration() : 0;
                        livingTarget.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, 2, false, false), player);

                        if (livingTarget.hasEffect(MobEffects.WEAKNESS)) {
                            PacketDistributor.sendToPlayer((ServerPlayer) player, new StaminaRecoverPayload(80));
                            BulletTimeHandler.addScale(0.5F, 60);
                        }
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

    private static double calculateValue(LivingEntity living, Holder<Attribute> attribute, Predicate<AttributeModifier> predicate) {
        var attributeInstance = living.getAttribute(attribute);
        return calculateValue(attribute, living.getAttributeBaseValue(attribute), attributeInstance != null ? attributeInstance.getModifiers().stream().filter(predicate).toArray(AttributeModifier[]::new) : new AttributeModifier[]{});
    }

    private static double calculateValue(Holder<Attribute> attribute, double baseValue, AttributeModifier[] modifiers) {
        for (AttributeModifier attributemodifier : Arrays.stream(modifiers).filter(m -> m.operation() == AttributeModifier.Operation.ADD_VALUE).toArray(AttributeModifier[]::new)) {
            baseValue += attributemodifier.amount();
        }

        double value = baseValue;

        for (AttributeModifier attributemodifier : Arrays.stream(modifiers).filter(m -> m.operation() == AttributeModifier.Operation.ADD_MULTIPLIED_BASE).toArray(AttributeModifier[]::new)) {
            value += baseValue * attributemodifier.amount();
        }

        for (AttributeModifier attributemodifier : Arrays.stream(modifiers).filter(m -> m.operation() == AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL).toArray(AttributeModifier[]::new)) {
            value *= 1.0 + attributemodifier.amount();
        }

        return attribute.value().sanitizeValue(value);
    }
}
