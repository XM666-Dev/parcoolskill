package com.xm666.parcoolskill.handler;

import com.xm666.parcoolskill.network.SkillPayload;
import com.xm666.parcoolskill.skill.SlideSkill;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Predicate;

public class SkillHandler {
    public static void handlePayload(final SkillPayload payload, final IPayloadContext context) {
        var level = context.player().level();
        var sourceEntity = level.getEntity(payload.sourceEntity());
        var targetEntity = level.getEntity(payload.targetEntity());
        var type = SkillPayload.Type.values()[payload.skillType()];

        if (!(sourceEntity instanceof Player player)) return;

        if (type == SkillPayload.Type.CLEAVE_READY) {
            CleaveHandler.handleReady(player);
        }

        if (!(targetEntity instanceof Entity target)) return;

        if (type == SkillPayload.Type.DROPKICK || type == SkillPayload.Type.HEEL_HOOK || type == SkillPayload.Type.LEG_SWEEP) {
            var slideSkillType = SlideSkill.Type.values()[type.ordinal()];
            SlideSkillHandler.handleAttack(player, target, slideSkillType);
            return;
        }

        if (type == SkillPayload.Type.CLEAVE_ATTACK) {
            CleaveHandler.handleAttack(player, target);
        }
    }

    public static void use(SkillPayload.Type type, Player source) {
        PacketDistributor.sendToServer(new SkillPayload(type.ordinal(), source.getId(), 0));
    }

    public static void use(SkillPayload.Type type, Player source, Entity target) {
        PacketDistributor.sendToServer(new SkillPayload(type.ordinal(), source.getId(), target.getId()));
    }

    public static void addEffect(LivingEntity target, Entity source, Holder<MobEffect> effect, int duration) {
        addEffect(target, source, effect, duration, 0);
    }

    public static void addEffect(LivingEntity target, Entity source, Holder<MobEffect> effect, int duration, int amplifier) {
        var effectInstance = target.getEffect(effect);
        duration += effectInstance != null ? effectInstance.getDuration() : 0;
        target.addEffect(new MobEffectInstance(effect, duration, amplifier), source);
    }

    public static void knockback(LivingEntity target, LivingEntity source, double amount) {
        var knockback = source.getAttributeValue(Attributes.ATTACK_KNOCKBACK) + amount;
        var rotation = source.getYRot() * Mth.DEG_TO_RAD;
        target.knockback(knockback * 0.5, Mth.sin(rotation), -Mth.cos(rotation));
    }

    public static double calculateAttribute(LivingEntity living, Holder<Attribute> attribute, Predicate<AttributeModifier> predicate) {
        var attributeInstance = living.getAttribute(attribute);
        var baseValue = living.getAttributeBaseValue(attribute);
        var modifiers = attributeInstance != null
                ? attributeInstance.getModifiers().stream().filter(predicate).toArray(AttributeModifier[]::new)
                : new AttributeModifier[]{};
        return calculateAttribute(attribute, baseValue, modifiers);
    }

    private static double calculateAttribute(Holder<Attribute> attribute, double baseValue, AttributeModifier[] modifiers) {
        for (var attributeModifier : Arrays.stream(modifiers).filter(m -> m.operation() == AttributeModifier.Operation.ADD_VALUE).toArray(AttributeModifier[]::new)) {
            baseValue += attributeModifier.amount();
        }

        var value = baseValue;

        for (var attributeModifier : Arrays.stream(modifiers).filter(m -> m.operation() == AttributeModifier.Operation.ADD_MULTIPLIED_BASE).toArray(AttributeModifier[]::new)) {
            value += baseValue * attributeModifier.amount();
        }

        for (var attributeModifier : Arrays.stream(modifiers).filter(m -> m.operation() == AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL).toArray(AttributeModifier[]::new)) {
            value *= 1.0 + attributeModifier.amount();
        }

        return attribute.value().sanitizeValue(value);
    }

    public static double getEntityHitRange(Entity shooter, double distance) {
        var partialTick = TimeScaleHandler.getOriginalPartialTick(true);
        var hitResult = shooter.pick(distance, partialTick, false);
        if (hitResult.getType() != HitResult.Type.MISS) {
            var eyePosition = shooter.getEyePosition(partialTick);
            return hitResult.getLocation().distanceTo(eyePosition);
        }
        return distance;
    }

    public static Entity[] getEntityHits(Entity shooter, double distance, double inflationAmount, long limit) {
        var partialTick = TimeScaleHandler.getOriginalPartialTick(true);
        var eyePosition = shooter.getEyePosition();
        var viewVector = shooter.getViewVector(partialTick);
        var hitVector = viewVector.scale(distance);
        var hitPosition = eyePosition.add(hitVector);
        var aabb = shooter.getBoundingBox().expandTowards(hitVector).inflate(1.0);
        return SkillHandler.getEntityHits(
                shooter,
                eyePosition,
                hitPosition,
                aabb,
                (entity) -> !entity.isSpectator() && entity.isPickable(),
                inflationAmount,
                limit
        );
    }

    public static Entity[] getEntityHits(Entity shooter, Vec3 startPosition, Vec3 endPosition, AABB boundingBox, Predicate<Entity> filter, double inflationAmount, long limit) {
        record HitResult(Entity entity, double distanceSquare) {
        }

        var level = shooter.level();
        var hitResults = new ArrayList<HitResult>();

        for (Entity entity : level.getEntities(shooter, boundingBox, filter)) {
            var aabb = entity.getBoundingBox().inflate(entity.getPickRadius() + inflationAmount);
            var optionalPoint = aabb.clip(startPosition, endPosition);
            double distanceSquare;
            if (aabb.contains(startPosition)) {
                distanceSquare = 0.0;
            } else if (optionalPoint.isPresent()) {
                var point = optionalPoint.get();
                distanceSquare = startPosition.distanceToSqr(point);
            } else {
                continue;
            }
            hitResults.add(new HitResult(entity, distanceSquare));
        }

        return hitResults.stream()
                .sorted(Comparator.comparingDouble((HitResult hitResult) -> hitResult.distanceSquare))
                .map(hitResult -> hitResult.entity)
                .limit(limit)
                .toArray(Entity[]::new);
    }

    public static float getRedComponent(int color) {
        return ((color >> 16) & 0xFF) / 255.0F;
    }

    public static float getGreenComponent(int color) {
        return ((color >> 8) & 0xFF) / 255.0F;
    }

    public static float getBlueComponent(int color) {
        return (color & 0xFF) / 255.0F;
    }
}
