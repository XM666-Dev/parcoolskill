package com.xm666.parcoolskill.handler;

import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import com.alrex.parcool.common.action.impl.CatLeap;
import com.alrex.parcool.common.action.impl.Dodge;
import com.alrex.parcool.common.action.impl.Slide;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.xm666.parcoolskill.Config;
import com.xm666.parcoolskill.ParCoolSkill;
import com.xm666.parcoolskill.damage.DamageTypes;
import com.xm666.parcoolskill.effect.Effects;
import com.xm666.parcoolskill.network.SkillAttackPayload;
import com.xm666.parcoolskill.skill.DodgeSkill;
import com.xm666.parcoolskill.skill.LeapSkill;
import com.xm666.parcoolskill.skill.SlideSkill;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.Arrays;
import java.util.function.Predicate;

@EventBusSubscriber(modid = ParCoolSkill.MODID)
public class SlideSkillHandler {
    @SubscribeEvent
    static void onSlideStart(ParCoolActionEvent.Start.Pre event) {
        if (!(event.getAction() instanceof SlideSkill slideSkill)) return;

        var player = event.getPlayer();
        var readyAttackType = SlideSkill.ReadyAttackType.NONE;

        var catleap = Parkourability.get(player).get(CatLeap.class);
        if (catleap.isDoing()) {
            var skillLeap = (LeapSkill) catleap;
            if (!skillLeap.parcoolskill$isAttackReady()) return;

            readyAttackType = SlideSkill.ReadyAttackType.DROPKICK;
            player.setDeltaMovement(player.getDeltaMovement().add(0.0, 0.2, 0.0));
        } else {
            var dodge = Parkourability.get(player).get(Dodge.class);
            if (!dodge.isDoing()) return;

            var skillDodge = (DodgeSkill) dodge;
            if (!skillDodge.parcoolskill$isAttackReady()) return;

            readyAttackType = SlideSkill.ReadyAttackType.HEEL_HOOK;
        }

        var slideSkillInvulnerableDuration = Config.SLIDE_SKILL_INVULNERABLE_DURATION.get();
        slideSkill.parcoolskill$setReadyAttackType(readyAttackType);
        slideSkill.parcoolskill$setInvulnerableTime(slideSkillInvulnerableDuration);
    }

    @SubscribeEvent
    static void onSlideFinish(ParCoolActionEvent.Finish.Pre event) {
        if (!(event.getAction() instanceof SlideSkill slideSkill)) return;

        slideSkill.parcoolskill$setReadyAttackType(SlideSkill.ReadyAttackType.NONE);
    }

    @SubscribeEvent
    static void onSlideTick(ParCoolActionEvent.Tick.Pre event) {
        if (!(event.getAction() instanceof Slide slide) || slide.isDoing()) return;

        var skillSlide = (SlideSkill) slide;
        var invulnerableTime = skillSlide.parcoolskill$getInvulnerableTime();
        if (invulnerableTime == 0) return;

        skillSlide.parcoolskill$setInvulnerableTime(invulnerableTime - 1);
    }

    @SubscribeEvent
    static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player) || event.getSource().is(DamageTypeTags.BYPASSES_ARMOR))
            return;

        var skillSlide = (SlideSkill) Parkourability.get(player).get(Slide.class);
        if (skillSlide.parcoolskill$getInvulnerableTime() == 0) return;

        event.setCanceled(true);
    }

    public static void handleAttack(Entity target, Player player, SkillAttackPayload.SkillAttackType skillAttackType) {
        var slideSkillBaseDamage = Config.SLIDE_SKILL_BASE_DAMAGE.get();
        var level = target.level();
        var attackModifierPredicate = (Predicate<AttributeModifier>) m -> !m.is(ResourceLocation.parse("minecraft:base_attack_damage"));
        var lowerArmorPredicate = (Predicate<AttributeModifier>) m -> m.is(ResourceLocation.parse("minecraft:armor.leggings")) || m.is(ResourceLocation.parse("minecraft:armor.boots"));
        var damage = (float) (slideSkillBaseDamage +
                calculateAttribute(player, Attributes.ATTACK_DAMAGE, attackModifierPredicate) +
                calculateAttribute(player, Attributes.ARMOR, lowerArmorPredicate) +
                calculateAttribute(player, Attributes.ARMOR_TOUGHNESS, lowerArmorPredicate));
        var slideAttack = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.SLIDE_ATTACK);
        var damageSource = new DamageSource(slideAttack, player, player, player.position());
        target.hurt(damageSource, damage);
        player.resetAttackStrengthTicker();

        if (target instanceof LivingEntity living) {
            var slideSkillBulletTimeScale = Config.SLIDE_SKILL_BULLET_TIME_SCALE.get().floatValue();
            var slideSkillBulletTimeDuration = Config.SLIDE_SKILL_BULLET_TIME_DURATION.get();
            switch (skillAttackType) {
                case DROPKICK -> {
                    var dropkickBaseKnockback = Config.DROPKICK_BASE_KNOCKBACK.get();
                    var knockback = dropkickBaseKnockback + player.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
                    var rotation = player.getYRot() * Mth.DEG_TO_RAD;
                    living.knockback(knockback * 0.5, Mth.sin(rotation), -Mth.cos(rotation));

                    if (living.hasEffect(Effects.VULNERABLE)) {
                        StaminaHandler.recoverStaminaOf(player, CatLeap.class);
                        TimeScaleHandler.applyScale(slideSkillBulletTimeScale, slideSkillBulletTimeDuration);
                    }
                }
                case HEEL_HOOK -> {
                    var heelHookSlowdownDuration = Config.HEEL_HOOK_SLOWDOWN_DURATION.get();
                    var heelHookSlowdownAmplifier = Config.HEEL_HOOK_SLOWDOWN_AMPLIFIER.get();
                    SkillHandler.addEffect(living, player, MobEffects.MOVEMENT_SLOWDOWN, heelHookSlowdownDuration, heelHookSlowdownAmplifier);

                    if (living.hasEffect(MobEffects.WEAKNESS)) {
                        StaminaHandler.recoverStaminaOf(player, Dodge.class);
                        TimeScaleHandler.applyScale(slideSkillBulletTimeScale, slideSkillBulletTimeDuration);
                    }
                }
            }
        }

        var sound = switch (skillAttackType) {
            case DROPKICK -> SoundEvents.PLAYER_ATTACK_KNOCKBACK;
            case HEEL_HOOK -> SoundEvents.PLAYER_ATTACK_STRONG;
            default -> null;
        };
        level.playSound(null, player.getX(), player.getY(), player.getZ(), sound, player.getSoundSource(), 1.0F, 1.0F);
    }

    private static double calculateAttribute(LivingEntity living, Holder<Attribute> attribute, Predicate<AttributeModifier> predicate) {
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
}
