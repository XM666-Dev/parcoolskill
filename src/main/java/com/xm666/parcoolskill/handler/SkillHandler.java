package com.xm666.parcoolskill.handler;

import com.alrex.parcool.common.capability.Parkourability;
import com.xm666.parcoolskill.network.PayloadHandler;
import com.xm666.parcoolskill.network.SkillPayload;
import com.xm666.parcoolskill.skill.SlideSkill;
import com.xm666.parcoolskill.skill.handler.CleaveHandler;
import com.xm666.parcoolskill.skill.handler.SlideSkillHandler;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SkillHandler {
    private static final Parkourability emptyParkourability = new Parkourability();

    public static void handlePayload(final SkillPayload payload, final Supplier<NetworkEvent.Context> context) {
        var level = context.get().getSender().level();
        var source = level.getEntity(payload.sourceEntity());
        var target = level.getEntity(payload.targetEntity());
        var type = SkillPayload.Type.values()[payload.skillType()];

        if (!(source instanceof Player player)) return;

        if (tryHandleCleaveReady(type, player)) return;

        if (target == null) return;

        if (tryHandleSlideSkill(type, player, target)) return;

        tryHandleCleaveAttack(type, player, target);
    }

    public static void use(SkillPayload.Type type, Player source) {
        PayloadHandler.INSTANCE.sendToServer(new SkillPayload(type.ordinal(), source.getId(), 0));
    }

    public static void use(SkillPayload.Type type, Player source, Entity target) {
        PayloadHandler.INSTANCE.sendToServer(new SkillPayload(type.ordinal(), source.getId(), target.getId()));
    }

    public static void knockback(LivingEntity target, LivingEntity source, double amount) {
        var knockback = source.getAttributeValue(Attributes.ATTACK_KNOCKBACK) + amount;
        var rotation = source.getYRot() * Mth.DEG_TO_RAD;
        target.knockback(knockback * 0.5, Mth.sin(rotation), -Mth.cos(rotation));
    }

    public static void addEffect(LivingEntity target, Entity source, MobEffect effect, int duration) {
        addEffect(target, source, effect, duration, 0);
    }

    public static void addEffect(LivingEntity target, Entity source, MobEffect effect, int duration, int amplifier) {
        var effectInstance = target.getEffect(effect);
        duration += effectInstance != null ? effectInstance.getDuration() : 0;
        target.addEffect(new MobEffectInstance(effect, duration, amplifier), source);
    }

    public static boolean isDamageSourceBlocked(LivingEntity living, DamageSource damageSource) {
        var entity = damageSource.getDirectEntity();
        if (damageSource.is(DamageTypeTags.BYPASSES_SHIELD)
                || entity instanceof AbstractArrow arrow && arrow.getPierceLevel() > 0) return false;

        var sourcePosition = damageSource.getSourcePosition();
        if (sourcePosition == null) return false;

        var viewVector = living.calculateViewVector(0.0F, living.getYHeadRot());
        var difference = sourcePosition.vectorTo(living.position());
        difference = new Vec3(difference.x, 0.0, difference.z).normalize();
        return difference.dot(viewVector) < 0.0;
    }

    public static Parkourability getParkourability(Player player) {
        var parkourability = Parkourability.get(player);
        return parkourability != null ? parkourability : emptyParkourability;
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
