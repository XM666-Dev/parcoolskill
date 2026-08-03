package com.xm666.parcoolskill.handler;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.Arrays;
import java.util.function.Predicate;

public class AttributeHandler {
    public static double calculateAttribute(LivingEntity living, Attribute attribute, Predicate<AttributeModifier> predicate) {
        var baseValue = living.getAttributeBaseValue(attribute);
        var modifiers = getAttributeModifiers(living, attribute, predicate);
        return calculateAttribute(baseValue, modifiers, attribute);
    }

    public static double getAttributeAddition(LivingEntity living, Attribute attribute) {
        return Math.max(living.getAttributeValue(attribute) - living.getAttributes().supplier.getBaseValue(attribute), 0.0);
    }

    private static double calculateAttribute(double baseValue, AttributeModifier[] modifiers, Attribute attribute) {
        return attribute.sanitizeValue(calculateAttribute(baseValue, modifiers));
    }

    private static double calculateAttribute(double baseValue, AttributeModifier[] modifiers) {
        for (var attributeModifier : Arrays.stream(modifiers).filter(m -> m.getOperation() == AttributeModifier.Operation.ADDITION).toArray(AttributeModifier[]::new)) {
            baseValue += attributeModifier.getAmount();
        }

        var value = baseValue;

        for (var attributeModifier : Arrays.stream(modifiers).filter(m -> m.getOperation() == AttributeModifier.Operation.MULTIPLY_BASE).toArray(AttributeModifier[]::new)) {
            value += baseValue * attributeModifier.getAmount();
        }

        for (var attributeModifier : Arrays.stream(modifiers).filter(m -> m.getOperation() == AttributeModifier.Operation.MULTIPLY_TOTAL).toArray(AttributeModifier[]::new)) {
            value *= 1.0 + attributeModifier.getAmount();
        }

        return value;
    }

    private static AttributeModifier[] getAttributeModifiers(LivingEntity living, Attribute attribute, Predicate<AttributeModifier> predicate) {
        var attributeInstance = living.getAttribute(attribute);
        if (attributeInstance == null) return new AttributeModifier[0];

        return attributeInstance.getModifiers().stream().filter(predicate).toArray(AttributeModifier[]::new);
    }
}
