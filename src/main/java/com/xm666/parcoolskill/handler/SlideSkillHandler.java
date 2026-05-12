package com.xm666.parcoolskill.handler;

import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import com.alrex.parcool.common.action.BehaviorEnforcer;
import com.alrex.parcool.common.action.impl.CatLeap;
import com.alrex.parcool.common.action.impl.Dodge;
import com.alrex.parcool.common.action.impl.Slide;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.xm666.parcoolskill.Config;
import com.xm666.parcoolskill.ParCoolSkill;
import com.xm666.parcoolskill.damage.DamageTypes;
import com.xm666.parcoolskill.effect.Effects;
import com.xm666.parcoolskill.network.SkillParticlePayload;
import com.xm666.parcoolskill.skill.DodgeSkill;
import com.xm666.parcoolskill.skill.LeapSkill;
import com.xm666.parcoolskill.skill.SlideSkill;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.function.Predicate;

@EventBusSubscriber(modid = ParCoolSkill.MODID)
public class SlideSkillHandler {
    private static final BehaviorEnforcer.ID ID_DESCEND_EDGE = BehaviorEnforcer.newID();

    @SubscribeEvent
    public static void onSlideStart(ParCoolActionEvent.Start.Pre event) {
        if (!(event.getAction() instanceof Slide slide)) return;

        var slideSkill = (SlideSkill) slide;
        var player = event.getPlayer();
        var parkourability = Parkourability.get(player);
        var readyType = SlideSkill.Type.NONE;

        var catleap = parkourability.get(CatLeap.class);
        if (catleap.isDoing()) {
            var leapSkill = (LeapSkill) catleap;
            if (!leapSkill.parcoolskill$isAttackReady()) return;

            readyType = SlideSkill.Type.DROPKICK;
            player.setDeltaMovement(player.getDeltaMovement().add(0.0, 0.2, 0.0));
        } else if (Config.HEEL_HOOK_ENABLED.get()) {
            var dodge = parkourability.get(Dodge.class);
            if (!dodge.isDoing()) return;

            var dodgeSkill = (DodgeSkill) dodge;
            if (!dodgeSkill.parcoolskill$isAttackReady()) return;

            readyType = SlideSkill.Type.HEEL_HOOK;
            if (!parkourability.getClientInfo().get(ParCoolConfig.Client.Booleans.CanGetOffStepsWhileDodge)) {
                parkourability.getBehaviorEnforcer().addMarkerCancellingDescendFromEdge(ID_DESCEND_EDGE, slide::isDoing);
            }
        }

        if (readyType == SlideSkill.Type.NONE) return;

        var slideSkillExtraInvulnerableDuration = Config.SLIDE_SKILL_EXTRA_INVULNERABLE_DURATION.get();
        slideSkill.parcoolskill$setReadyType(readyType);
        slideSkill.parcoolskill$setInvulnerableTime(slideSkillExtraInvulnerableDuration);
    }

    @SubscribeEvent
    public static void onSlideTryToContinue(ParCoolActionEvent.TryToContinue event) {
        if (!(event.getAction() instanceof SlideSkill)) return;

        var player = event.getPlayer();
        var catLeap = Parkourability.get(player).get(CatLeap.class);
        if (catLeap.isDoing() || catLeap.getNotDoingTick() > 0) return;

        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onSlideFinish(ParCoolActionEvent.Finish.Pre event) {
        if (!(event.getAction() instanceof SlideSkill slideSkill)) return;

        slideSkill.parcoolskill$setReadyType(SlideSkill.Type.NONE);
    }

    @SubscribeEvent
    public static void onSlideTick(ParCoolActionEvent.Tick.Pre event) {
        if (!(event.getAction() instanceof Slide slide) || slide.isDoing()) return;

        var slideSkill = (SlideSkill) slide;
        var invulnerableTime = slideSkill.parcoolskill$getInvulnerableTime();
        if (invulnerableTime == 0) return;

        slideSkill.parcoolskill$setInvulnerableTime(invulnerableTime - 1);
    }

    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player) || event.getSource().is(DamageTypeTags.BYPASSES_ARMOR))
            return;

        var slideSkill = (SlideSkill) Parkourability.get(player).get(Slide.class);
        if (slideSkill.parcoolskill$getInvulnerableTime() == 0) return;

        event.setCanceled(true);
    }

    @SuppressWarnings({"WrapperTypeMayBePrimitive", "DataFlowIssue"})
    public static void handleAttack(Player player, Entity target, SlideSkill.Type type) {
        if (!isReadyForAttack(player, type)) return;

        var aabb = target.getBoundingBox();
        if (!player.isWithinEntityInteractionRange(aabb, 1.0)) return;

        StaminaHandler.consume(player, switch (type) {
            case DROPKICK -> Config.DROPKICK_STAMINA_CONSUMPTION.get();
            case HEEL_HOOK -> Config.HEEL_HOOK_STAMINA_CONSUMPTION.get();
            default -> 0;
        });

        var slideSkillDamageHealthGrowth = Config.SLIDE_SKILL_DAMAGE_HEALTH_GROWTH.get();
        var level = target.level();
        var lowerArmorPredicate = (Predicate<AttributeModifier>) m -> m.is(Identifier.parse("minecraft:armor.leggings")) || m.is(Identifier.parse("minecraft:armor.boots"));
        var attackModifierPredicate = (Predicate<AttributeModifier>) m -> !m.is(Identifier.parse("minecraft:base_attack_damage"));
        var damage = (float) (
                player.getAttributeValue(Attributes.MAX_HEALTH) * slideSkillDamageHealthGrowth +
                        SkillHandler.calculateAttribute(player, Attributes.ARMOR, lowerArmorPredicate) +
                        SkillHandler.calculateAttribute(player, Attributes.ATTACK_DAMAGE, attackModifierPredicate)
        );
        var slideAttack = level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(DamageTypes.SLIDE_ATTACK);
        var damageSource = new DamageSource(slideAttack, player, player, player.position());
        target.hurtServer((ServerLevel) level, damageSource, damage);
        player.resetAttackStrengthTicker();

        if (target instanceof LivingEntity living) {
            var slideSkillBulletTimeScale = Config.SLIDE_SKILL_BULLET_TIME_SCALE.get().floatValue();
            var slideSkillBulletTimeDuration = Config.SLIDE_SKILL_BULLET_TIME_DURATION.get();
            switch (type) {
                case DROPKICK -> {
                    var dropkickKnockbackBase = Config.DROPKICK_KNOCKBACK_BASE.get();
                    SkillHandler.knockback(living, player, dropkickKnockbackBase);

                    if (living.hasEffect(Effects.VULNERABLE)) {
                        var dropkickStaminaConsumption = Config.DROPKICK_STAMINA_CONSUMPTION.get();
                        StaminaHandler.recover(player, StaminaHandler.getConsumptionOf(player, CatLeap.class) + StaminaHandler.getConsumptionOf(player, Slide.class) + dropkickStaminaConsumption);
                        TimeScaleHandler.applyScale(slideSkillBulletTimeScale, slideSkillBulletTimeDuration);
                    }

                    SkillParticleHandler.emit(SkillParticlePayload.Type.IRONCLAD_HIT, target);
                }
                case HEEL_HOOK -> {
                    var heelHookSlowdownDuration = Config.HEEL_HOOK_SLOWDOWN_DURATION.get();
                    var heelHookSlowdownAmplifier = Config.HEEL_HOOK_SLOWDOWN_AMPLIFIER.get();
                    SkillHandler.addEffect(living, player, MobEffects.SLOWNESS, heelHookSlowdownDuration, heelHookSlowdownAmplifier);

                    if (living.hasEffect(MobEffects.WEAKNESS) || living.hasEffect(Effects.NEUTRALIZED)) {
                        var heelHookStaminaConsumption = Config.HEEL_HOOK_STAMINA_CONSUMPTION.get();
                        StaminaHandler.recover(player, StaminaHandler.getConsumptionOf(player, Dodge.class) + StaminaHandler.getConsumptionOf(player, Slide.class) + heelHookStaminaConsumption);
                        TimeScaleHandler.applyScale(slideSkillBulletTimeScale, slideSkillBulletTimeDuration);
                    }

                    SkillParticleHandler.emit(SkillParticlePayload.Type.SILENT_HIT, target);
                }
            }
        }

        var sound = switch (type) {
            case DROPKICK -> SoundEvents.PLAYER_ATTACK_KNOCKBACK;
            case HEEL_HOOK -> SoundEvents.PLAYER_ATTACK_STRONG;
            default -> null;
        };
        level.playSound(null, player.getX(), player.getY(), player.getZ(), sound, player.getSoundSource(), 1.0F, 1.0F);
    }

    public static boolean isReadyForAttack(Player player, SlideSkill.Type type) {
        var slideSkill = (SlideSkill) Parkourability.get(player).get(Slide.class);
        var readyType = slideSkill.parcoolskill$getReadyType();
        if (readyType != type) return false;

        slideSkill.parcoolskill$setReadyType(SlideSkill.Type.NONE);
        return true;
    }
}
