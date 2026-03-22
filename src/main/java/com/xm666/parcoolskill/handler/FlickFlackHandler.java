package com.xm666.parcoolskill.handler;

import com.alrex.parcool.api.Stamina;
import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import com.alrex.parcool.common.action.impl.Crawl;
import com.alrex.parcool.common.action.impl.Flipping;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.xm666.parcoolskill.Config;
import com.xm666.parcoolskill.ParCoolSkill;
import com.xm666.parcoolskill.damage.DamageTypes;
import com.xm666.parcoolskill.effect.Effects;
import com.xm666.parcoolskill.network.SkillParticlePayload;
import com.xm666.parcoolskill.skill.FlipSkill;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.function.Predicate;

@EventBusSubscriber(modid = ParCoolSkill.MODID)
public class FlickFlackHandler {
    @SubscribeEvent
    public static void onFlippingStart(ParCoolActionEvent.Start.Pre event) {
        if (!(event.getAction() instanceof FlipSkill flipSkill)) return;

        var player = event.getPlayer();
        var parkourability = Parkourability.get(player);
        var crawl = parkourability.get(Crawl.class);
        if (crawl.getNotDoingTick() >= 10) return;

        var flickFlackStaminaConsumption = Config.FLICK_FLACK_STAMINA_CONSUMPTION.get();
        var stamina = Stamina.get(player);
        stamina.consume(flickFlackStaminaConsumption);
        if (stamina.isExhausted()) return;

        flipSkill.parcoolskill$setAttackReady(true);
        flipSkill.parcoolskill$setInvulnerableTime(10);

        var movement = player.getDeltaMovement();
        var lookAngle = player.getLookAngle();
        var lookAngleVector = new Vec2((float) lookAngle.x, (float) lookAngle.z);
        var lookAngleDirection = lookAngleVector.normalized().scale(0.5F);
        player.setDeltaMovement(lookAngleDirection.x, movement.y, lookAngleDirection.y);
    }

    @SubscribeEvent
    public static void onFlippingFinish(ParCoolActionEvent.Finish.Pre event) {
        if (!(event.getAction() instanceof FlipSkill flipSkill)) return;

        flipSkill.parcoolskill$setAttackReady(false);
    }

    @SubscribeEvent
    public static void onSlideTick(ParCoolActionEvent.Tick.Pre event) {
        if (!(event.getAction() instanceof Flipping flipping) || flipping.isDoing()) return;

        var flipSkill = (FlipSkill) flipping;
        var invulnerableTime = flipSkill.parcoolskill$getInvulnerableTime();
        if (invulnerableTime == 0) return;

        flipSkill.parcoolskill$setInvulnerableTime(invulnerableTime - 1);
    }

    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player) || event.getSource().is(DamageTypeTags.BYPASSES_ARMOR))
            return;

        var flipSkill = (FlipSkill) Parkourability.get(player).get(Flipping.class);
        if (flipSkill.parcoolskill$getInvulnerableTime() == 0) return;

        event.setCanceled(true);
    }

    public static void handleAttack(Player player, Entity target) {
        if (!isReadyForAttack(player)) return;

        var aabb = target.getBoundingBox();
        if (!player.canInteractWithEntity(aabb, 1.0)) return;

        var flipSkillDamageAddition = Config.FLIP_SKILL_DAMAGE_ADDITION.get();
        var level = target.level();
        var attackModifierPredicate = (Predicate<AttributeModifier>) m -> !m.is(ResourceLocation.parse("minecraft:base_attack_damage"));
        var upperArmorPredicate = (Predicate<AttributeModifier>) m -> m.is(ResourceLocation.parse("minecraft:armor.helmet")) || m.is(ResourceLocation.parse("minecraft:armor.chestplate"));
        var damage = (float) (
                SkillHandler.calculateAttribute(player, Attributes.ATTACK_DAMAGE, attackModifierPredicate) +
                        SkillHandler.calculateAttribute(player, Attributes.ARMOR, upperArmorPredicate) +
                        SkillHandler.calculateAttribute(player, Attributes.ARMOR_TOUGHNESS, upperArmorPredicate) +
                        flipSkillDamageAddition
        );
        var flipAttack = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.FLIP_ATTACK);
        var damageSource = new DamageSource(flipAttack, player, player, player.position());
        target.hurt(damageSource, damage);
        player.resetAttackStrengthTicker();

        var flickFlackNeutralizedDuration = Config.FLICK_FLACK_NEUTRALIZED_DURATION.get();
        for (var living : target.level().getEntitiesOfClass(LivingEntity.class, getSweepHitBox(target))) {
            var entityReachSquare = Mth.square(player.entityInteractionRange());
            if (living != player && !player.isAlliedTo(living) && (!(living instanceof ArmorStand) || !((ArmorStand) living).isMarker()) && player.distanceToSqr(living) < entityReachSquare) {
                living.hurt(damageSource, damage);
                SkillHandler.addEffect(living, player, Effects.NEUTRALIZED, flickFlackNeutralizedDuration);

                SkillParticleHandler.emit(SkillParticlePayload.Type.SILENT_HIT, target);
            }
        }
        player.sweepAttack();

        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_KNOCKBACK, player.getSoundSource(), 1.0F, 1.0F);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isReadyForAttack(Player player) {
        var flipSkill = (FlipSkill) Parkourability.get(player).get(Flipping.class);
        if (!flipSkill.parcoolskill$isAttackReady()) return false;

        flipSkill.parcoolskill$setAttackReady(false);
        return true;
    }

    private static AABB getSweepHitBox(Entity target) {
        return target.getBoundingBox().inflate(1.0, 0.25, 1.0);
    }
}
