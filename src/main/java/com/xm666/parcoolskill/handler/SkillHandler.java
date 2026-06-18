package com.xm666.parcoolskill.handler;

import com.xm666.parcoolskill.network.SkillPayload;
import com.xm666.parcoolskill.skill.SlideSkill;
import com.xm666.parcoolskill.skill.handler.CleaveHandler;
import com.xm666.parcoolskill.skill.handler.SlideSkillHandler;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SkillHandler {
    public static void handlePayload(final SkillPayload payload, final IPayloadContext context) {
        var level = context.player().level();
        var sourceEntity = level.getEntity(payload.sourceEntity());
        var targetEntity = level.getEntity(payload.targetEntity());
        var type = SkillPayload.Type.values()[payload.skillType()];

        if (!(sourceEntity instanceof Player player)) return;

        if (tryHandleCleaveReady(type, player)) return;

        if (!(targetEntity instanceof Entity target)) return;

        if (tryHandleSlideSkill(type, player, target)) return;

        tryHandleCleaveAttack(type, player, target);
    }

    public static void use(SkillPayload.Type type, Player source) {
        PacketDistributor.sendToServer(new SkillPayload(type.ordinal(), source.getId(), 0));
    }

    public static void use(SkillPayload.Type type, Player source, Entity target) {
        PacketDistributor.sendToServer(new SkillPayload(type.ordinal(), source.getId(), target.getId()));
    }

    public static void knockback(LivingEntity target, LivingEntity source, double amount) {
        var knockback = source.getAttributeValue(Attributes.ATTACK_KNOCKBACK) + amount;
        var rotation = source.getYRot() * Mth.DEG_TO_RAD;
        target.knockback(knockback * 0.5, Mth.sin(rotation), -Mth.cos(rotation));
    }

    public static void addEffect(LivingEntity target, Entity source, Holder<MobEffect> effect, int duration) {
        addEffect(target, source, effect, duration, 0);
    }

    public static void addEffect(LivingEntity target, Entity source, Holder<MobEffect> effect, int duration, int amplifier) {
        var effectInstance = target.getEffect(effect);
        duration += effectInstance != null ? effectInstance.getDuration() : 0;
        target.addEffect(new MobEffectInstance(effect, duration, amplifier), source);
    }

    private static boolean tryHandleCleaveReady(SkillPayload.Type type, Player player) {
        if (type != SkillPayload.Type.CLEAVE_READY) return false;

        CleaveHandler.handleReady(player);
        return true;
    }

    private static boolean tryHandleSlideSkill(SkillPayload.Type type, Player player, Entity target) {
        if (type != SkillPayload.Type.DROPKICK && type != SkillPayload.Type.HEEL_HOOK) return false;

        var slideSkillType = SlideSkill.Type.values()[type.ordinal()];
        SlideSkillHandler.handleAttack(player, target, slideSkillType);
        return true;
    }

    private static boolean tryHandleCleaveAttack(SkillPayload.Type type, Player player, Entity target) {
        if (type != SkillPayload.Type.CLEAVE_ATTACK) return false;

        CleaveHandler.handleAttack(player, target);
        return true;
    }
}
