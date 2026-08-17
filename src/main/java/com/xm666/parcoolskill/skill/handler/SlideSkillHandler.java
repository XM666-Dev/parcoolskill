package com.xm666.parcoolskill.skill.handler;

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
import com.xm666.parcoolskill.handler.AttributeHandler;
import com.xm666.parcoolskill.handler.SkillHandler;
import com.xm666.parcoolskill.handler.StaminaHandler;
import com.xm666.parcoolskill.network.SkillParticlePayload;
import com.xm666.parcoolskill.particle.SkillParticleHandler;
import com.xm666.parcoolskill.skill.DodgeSkill;
import com.xm666.parcoolskill.skill.LeapSkill;
import com.xm666.parcoolskill.skill.SlideSkill;
import com.xm666.timescalelib.handler.TimeScaleHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber(modid = ParCoolSkill.MODID)
public class SlideSkillHandler {
    private static final BehaviorEnforcer.ID ID_DESCEND_EDGE = BehaviorEnforcer.newID();

    @SubscribeEvent
    public static void onSlideStart(ParCoolActionEvent.Start.Pre event) {
        if (!(event.getAction() instanceof Slide slide)) return;

        var slideSkill = (SlideSkill) slide;
        var player = event.getPlayer();
        var parkourability = Parkourability.get(player);
        SlideSkill.Type readyType;
        if (canUseDropKick(parkourability)) {
            readyType = SlideSkill.Type.DROPKICK;
            player.setDeltaMovement(player.getDeltaMovement().add(0.0, 0.2, 0.0));
        } else if (canUseHeelHook(parkourability)) {
            readyType = SlideSkill.Type.HEEL_HOOK;
            if (!parkourability.getClientInfo().get(ParCoolConfig.Client.Booleans.CanGetOffStepsWhileDodge)) {
                parkourability.getBehaviorEnforcer().addMarkerCancellingDescendFromEdge(ID_DESCEND_EDGE, slide::isDoing);
            }
        } else {
            return;
        }

        var slideSkillExtraInvulnerableDuration = Config.SLIDE_SKILL_EXTRA_INVULNERABLE_DURATION.get();
        slideSkill.parcoolskill$setReadyType(readyType);
        slideSkill.parcoolskill$setInvulnerableTime(slideSkillExtraInvulnerableDuration);
        slideSkill.parcoolskill$setInvulnerable(true);
    }

    @SubscribeEvent
    public static void onSlideTryToContinue(ParCoolActionEvent.TryToContinue event) {
        if (!(event.getAction() instanceof SlideSkill)) return;

        var player = event.getPlayer();
        var parkourability = Parkourability.get(player);
        var catLeap = parkourability.get(CatLeap.class);
        if (catLeap.isDoing() || catLeap.getNotDoingTick() > 0) return;

        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onSlideFinish(ParCoolActionEvent.Finish.Pre event) {
        if (!(event.getAction() instanceof SlideSkill slideSkill)) return;

        slideSkill.parcoolskill$setReadyType(SlideSkill.Type.NONE);
        slideSkill.parcoolskill$setInvulnerable(false);
    }

    @SubscribeEvent
    public static void onSlideTick(ParCoolActionEvent.Tick.Pre event) {
        if (!(event.getAction() instanceof SlideSkill slideSkill) || slideSkill.parcoolskill$isInvulnerable()) return;

        var invulnerableTime = slideSkill.parcoolskill$getInvulnerableTime();
        if (invulnerableTime == 0) return;

        slideSkill.parcoolskill$setInvulnerableTime(invulnerableTime - 1);
    }

    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player) || event.getSource().is(DamageTypeTags.BYPASSES_ARMOR))
            return;

        var parkourability = Parkourability.get(player);
        var slideSkill = (SlideSkill) parkourability.get(Slide.class);
        if (slideSkill.parcoolskill$getInvulnerableTime() == 0) return;

        event.setCanceled(true);
    }

    public static void handleAttack(Player player, Entity target, SlideSkill.Type type) {
        if (!isAttackReady(player, type)) return;

        var boundingBox = target.getBoundingBox();
        if (!player.isWithinEntityInteractionRange(boundingBox, 1.0)) return;

        StaminaHandler.consume(player, switch (type) {
            case DROPKICK -> Config.DROPKICK_STAMINA_CONSUMPTION.get();
            case HEEL_HOOK -> Config.HEEL_HOOK_STAMINA_CONSUMPTION.get();
            default -> 0;
        });

        var level = (ServerLevel) player.level();
        var damageSource = getDamageSource(player, type);
        var damage = getDamage(player);
        target.hurtServer(level, damageSource, damage);
        player.resetAttackStrengthTicker();

        if (target instanceof LivingEntity living) {
            switch (type) {
                case DROPKICK -> {
                    useDropKick(player, living);

                    SkillParticleHandler.emit(SkillParticlePayload.Type.IRONCLAD_HIT, target);
                }
                case HEEL_HOOK -> {
                    useHeelHook(player, living);

                    SkillParticleHandler.emit(SkillParticlePayload.Type.SILENT_HIT, target);
                }
            }
        }

        var sound = type == SlideSkill.Type.DROPKICK ? SoundEvents.PLAYER_ATTACK_KNOCKBACK : SoundEvents.PLAYER_ATTACK_STRONG;
        level.playSound(null, player.getX(), player.getY(), player.getZ(), sound, player.getSoundSource(), 1.0F, 1.0F);
    }

    public static SlideSkill.Type getReadyType(Player player) {
        var parkourability = Parkourability.get(player);
        var slideSkill = (SlideSkill) parkourability.get(Slide.class);
        var readyType = slideSkill.parcoolskill$getReadyType();
        slideSkill.parcoolskill$setReadyType(SlideSkill.Type.NONE);
        return readyType;
    }

    @OnlyIn(Dist.CLIENT)
    public static void tryPushBlock(BlockHitResult hitResult) {
        if (hitResult.getType() == HitResult.Type.MISS) return;

        var mc = Minecraft.getInstance();
        var player = mc.player;
        var parkourability = Parkourability.get(player);
        var slideSkill = (SlideSkill) parkourability.get(Slide.class);
        var readyType = slideSkill.parcoolskill$getReadyType();
        if (readyType == SlideSkill.Type.NONE) return;

        var level = mc.level;
        var blockPos = hitResult.getBlockPos();
        var blockState = level.getBlockState(blockPos);
        var block = blockState.getBlock();
        if (!(block instanceof DoorBlock doorBlock) || !doorBlock.type().canOpenByHand()) return;

        var direction = blockState.getValue(DoorBlock.FACING);
        var open = blockState.getValue(DoorBlock.OPEN);
        var openDirection = direction.getOpposite();
        if (open) {
            var hinge = blockState.getValue(DoorBlock.HINGE);
            openDirection = hinge == DoorHingeSide.LEFT ? openDirection.getClockWise() : openDirection.getCounterClockWise();
        }
        if (openDirection != hitResult.getDirection()) return;

        var sound = readyType == SlideSkill.Type.DROPKICK ? SoundEvents.PLAYER_ATTACK_KNOCKBACK : SoundEvents.PLAYER_ATTACK_STRONG;
        mc.gameMode.useItemOn(player, InteractionHand.MAIN_HAND, hitResult);
        level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, blockPos, Block.getId(blockState));
        level.playLocalSound(player, sound, player.getSoundSource(), 1.0F, 1.0F);
    }

    private static boolean canUseDropKick(Parkourability parkourability) {
        if (!Config.DROPKICK_ENABLED.get()) return false;

        var catleap = parkourability.get(CatLeap.class);
        if (!catleap.isDoing()) return false;

        var leapSkill = (LeapSkill) catleap;
        return leapSkill.parcoolskill$isAttackReady();
    }

    private static boolean canUseHeelHook(Parkourability parkourability) {
        if (!Config.HEEL_HOOK_ENABLED.get()) return false;

        var dodge = parkourability.get(Dodge.class);
        if (!dodge.isDoing()) return false;

        var dodgeSkill = (DodgeSkill) dodge;
        return dodgeSkill.parcoolskill$isAttackReady();
    }

    private static boolean isAttackReady(Player player, SlideSkill.Type type) {
        var parkourability = Parkourability.get(player);
        var slideSkill = (SlideSkill) parkourability.get(Slide.class);
        var readyType = slideSkill.parcoolskill$getReadyType();
        if (readyType != type) return false;

        slideSkill.parcoolskill$setReadyType(SlideSkill.Type.NONE);
        return true;
    }

    private static DamageSource getDamageSource(Player player, SlideSkill.Type type) {
        var level = player.level();
        var damageType = level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(
                type == SlideSkill.Type.DROPKICK ? DamageTypes.KICK_ATTACK : DamageTypes.TWIST_ATTACK
        );
        return new DamageSource(damageType, player, player, player.position());
    }

    private static float getDamage(Player player) {
        var slideSkillDamageBase = Config.SLIDE_SKILL_DAMAGE_BASE.get();
        var slideSkillDamageMultiplier = Config.SLIDE_SKILL_DAMAGE_MULTIPLIER.get();
        var attack = AttributeHandler.calculateAttribute(player, Attributes.ATTACK_DAMAGE, SlideSkillHandler::isExtraAttack);
        var armor = AttributeHandler.calculateAttribute(player, Attributes.ARMOR, SlideSkillHandler::isLowerArmor);
        var health = AttributeHandler.getAttributeAddition(player, Attributes.MAX_HEALTH);
        return (float) (slideSkillDamageBase + (attack + armor + health) * slideSkillDamageMultiplier);
    }

    private static boolean isLowerArmor(AttributeModifier attributeModifier) {
        return attributeModifier.is(Identifier.parse("minecraft:armor.leggings")) || attributeModifier.is(Identifier.parse("minecraft:armor.boots"));
    }

    private static boolean isExtraAttack(AttributeModifier attributeModifier) {
        return !attributeModifier.is(Identifier.parse("minecraft:base_attack_damage"));
    }

    private static void useDropKick(Player player, LivingEntity target) {
        var dropkickKnockbackBase = Config.DROPKICK_KNOCKBACK_BASE.get();
        SkillHandler.knockback(target, player, dropkickKnockbackBase);

        if (!target.hasEffect(Effects.VULNERABLE)) return;

        var slideSkillBulletTimeScale = Config.SLIDE_SKILL_BULLET_TIME_SCALE.get().floatValue();
        var slideSkillBulletTimeDuration = Config.SLIDE_SKILL_BULLET_TIME_DURATION.get();
        var dropkickStaminaConsumption = Config.DROPKICK_STAMINA_CONSUMPTION.get();
        var catLeapConsumption = StaminaHandler.getConsumptionOf(player, CatLeap.class);
        var slideConsumption = StaminaHandler.getConsumptionOf(player, Slide.class);
        StaminaHandler.recover(player, catLeapConsumption + slideConsumption + dropkickStaminaConsumption);
        TimeScaleHandler.applyScale(slideSkillBulletTimeScale, slideSkillBulletTimeDuration);
    }

    private static void useHeelHook(Player player, LivingEntity target) {
        var heelHookSlowdownDuration = Config.HEEL_HOOK_SLOWDOWN_DURATION.get();
        var heelHookSlowdownAmplifier = Config.HEEL_HOOK_SLOWDOWN_AMPLIFIER.get();
        SkillHandler.addEffect(target, player, MobEffects.SLOWNESS, heelHookSlowdownDuration, heelHookSlowdownAmplifier);

        if (!target.hasEffect(Effects.NEUTRALIZED)) return;

        var slideSkillBulletTimeScale = Config.SLIDE_SKILL_BULLET_TIME_SCALE.get().floatValue();
        var slideSkillBulletTimeDuration = Config.SLIDE_SKILL_BULLET_TIME_DURATION.get();
        var heelHookStaminaConsumption = Config.HEEL_HOOK_STAMINA_CONSUMPTION.get();
        var dodgeConsumption = StaminaHandler.getConsumptionOf(player, Dodge.class);
        var slideConsumption = StaminaHandler.getConsumptionOf(player, Slide.class);
        StaminaHandler.recover(player, dodgeConsumption + slideConsumption + heelHookStaminaConsumption);
        TimeScaleHandler.applyScale(slideSkillBulletTimeScale, slideSkillBulletTimeDuration);
    }
}
