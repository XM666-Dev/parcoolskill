package com.xm666.parcoolskill.handler;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.Arrays;
import java.util.function.Predicate;

public class AttributeHandler {
    public static double calculateAttribute(LivingEntity living, Holder<Attribute> attribute, Predicate<AttributeModifier> predicate) {
        var baseValue = living.getAttributeBaseValue(attribute);
        var modifiers = getAttributeModifiers(living, attribute, predicate);
        return calculateAttribute(baseValue, modifiers, attribute);
    }

    private static double calculateAttribute(double baseValue, AttributeModifier[] modifiers, Holder<Attribute> attribute) {
        return attribute.value().sanitizeValue(calculateAttribute(baseValue, modifiers));
    }

    private static double calculateAttribute(double baseValue, AttributeModifier[] modifiers) {
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

        return value;
    }

    private static AttributeModifier[] getAttributeModifiers(LivingEntity living, Holder<Attribute> attribute, Predicate<AttributeModifier> predicate) {
        var attributeInstance = living.getAttribute(attribute);
        if (attributeInstance == null) return new AttributeModifier[0];

        return attributeInstance.getModifiers().stream().filter(predicate).toArray(AttributeModifier[]::new);
    }

    public static double getAttributeAddition(LivingEntity living, Holder<Attribute> attribute) {
        return Math.max(living.getAttributeValue(attribute) - living.getAttributes().supplier.getBaseValue(attribute), 0.0);
    }
}
