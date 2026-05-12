package com.xm666.parcoolskill.handler;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import com.alrex.parcool.common.action.impl.Dodge;
import com.alrex.parcool.common.action.impl.Flipping;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.xm666.parcoolskill.Config;
import com.xm666.parcoolskill.ParCoolSkill;
import com.xm666.parcoolskill.effect.Effects;
import com.xm666.parcoolskill.event.PlayerAttackEvent;
import com.xm666.parcoolskill.network.SkillParticlePayload;
import com.xm666.parcoolskill.skill.FlippingSkill;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber(modid = ParCoolSkill.MODID)
public class FlickFlackHandler {
    @SubscribeEvent
    public static void onFlippingStart(ParCoolActionEvent.Start.Post event) {
        if (!(event.getAction() instanceof FlippingSkill flippingSkill)) return;

        var player = event.getPlayer();
        var parkourability = Parkourability.get(player);
        var dodge = parkourability.get(Dodge.class);
        if (!dodge.isDoing()) return;

        flippingSkill.parcoolskill$setAttackReady(true);
        flippingSkill.parcoolskill$setInvulnerableTime(20);

        var movement = player.getDeltaMovement();
        player.setDeltaMovement(movement.x, movement.y * 1.625, movement.z);

        if (!player.isLocalPlayer() || !ParCoolConfig.Client.Booleans.EnableActionSounds.get()) return;
        player.playSound(SoundEvents.VAULT.get(), 1.0F, 1.0F);
    }

    @SubscribeEvent
    public static void onFlippingFinish(ParCoolActionEvent.Finish.Pre event) {
        if (!(event.getAction() instanceof FlippingSkill flippingSkill)) return;

        flippingSkill.parcoolskill$setAttackReady(false);
    }

    @SubscribeEvent
    public static void onSlideTick(ParCoolActionEvent.Tick.Pre event) {
        if (!(event.getAction() instanceof Flipping flipping)) return;

        var flippingSkill = (FlippingSkill) flipping;
        var invulnerableTime = flippingSkill.parcoolskill$getInvulnerableTime();
        if (invulnerableTime == 0) return;

        flippingSkill.parcoolskill$setInvulnerableTime(invulnerableTime - 1);
    }

    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player) || event.getSource().is(DamageTypeTags.BYPASSES_ARMOR))
            return;

        var flippingSkill = (FlippingSkill) Parkourability.get(player).get(Flipping.class);
        if (flippingSkill.parcoolskill$getInvulnerableTime() == 0) return;

        event.setCanceled(true);
    }

    @SuppressWarnings("WrapperTypeMayBePrimitive")
    @SubscribeEvent
    public static void onPlayerAttack(PlayerAttackEvent.Pre event) {
        var player = event.getEntity();
        var flippingSkill = (FlippingSkill) Parkourability.get(player).get(Flipping.class);
        if (!isReadyForAttack(player) || !event.isFullStrength()) {
            if (flippingSkill.parcoolskill$disableCrit()) {
                event.setDisableCrit(true);
            }
            return;
        }

        var flickFlackStaminaConsumption = Config.FLICK_FLACK_STAMINA_CONSUMPTION.get();
        StaminaHandler.consume(player, flickFlackStaminaConsumption);

        var flickFlackNeutralizedDuration = Config.FLICK_FLACK_NEUTRALIZED_DURATION.get();
        var target = event.getTarget();
        if (target instanceof LivingEntity living) {
            SkillHandler.addEffect(living, player, Effects.NEUTRALIZED, flickFlackNeutralizedDuration);
        }

        SkillParticleHandler.emit(SkillParticlePayload.Type.SILENT_HIT, target);
        sweepAttack(player);
        event.setDisableCrit(true);

        flippingSkill.parcoolskill$setDisableCrit(true);
        for (var living : target.level().getEntitiesOfClass(LivingEntity.class, getSweepHitBox(target))) {
            var entityReachSquare = Mth.square(player.entityInteractionRange());
            if (living != player && living != target && !player.isAlliedTo(living) && (!(living instanceof ArmorStand) || !((ArmorStand) living).isMarker()) && player.distanceToSqr(living) < entityReachSquare) {
                player.attackStrengthTicker = (int) player.getCurrentItemAttackStrengthDelay();
                player.attack(living);
                SkillHandler.addEffect(living, player, Effects.NEUTRALIZED, flickFlackNeutralizedDuration);

                SkillParticleHandler.emit(SkillParticlePayload.Type.SILENT_HIT, living);
            }
        }
        flippingSkill.parcoolskill$setDisableCrit(false);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isReadyForAttack(Player player) {
        var flippingSkill = (FlippingSkill) Parkourability.get(player).get(Flipping.class);
        if (!flippingSkill.parcoolskill$isAttackReady()) return false;

        flippingSkill.parcoolskill$setAttackReady(false);
        return true;
    }

    private static AABB getSweepHitBox(Entity target) {
        return target.getBoundingBox().inflate(1.0, 0.25, 1.0);
    }

    private static void sweepAttack(Player player) {
        if (!(player.level() instanceof ServerLevel serverlevel)) return;

        var xOffset = -Mth.sin(player.getYRot() * Mth.DEG_TO_RAD);
        var zOffset = Mth.cos(player.getYRot() * Mth.DEG_TO_RAD);
        player.playSound(net.minecraft.sounds.SoundEvents.PLAYER_ATTACK_SWEEP);
        serverlevel.sendParticles(ParticleTypes.SWEEP_ATTACK, player.getX() + xOffset, player.getY(0.5F), player.getZ() + zOffset, 0, xOffset, 0.0F, zOffset, 0.0F);
    }
}
