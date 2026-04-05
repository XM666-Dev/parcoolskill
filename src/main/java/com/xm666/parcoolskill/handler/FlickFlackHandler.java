package com.xm666.parcoolskill.handler;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.api.Stamina;
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
import com.xm666.parcoolskill.network.SkillPayload;
import com.xm666.parcoolskill.skill.FlipSkill;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber(modid = ParCoolSkill.MODID)
public class FlickFlackHandler {
    @SubscribeEvent
    public static void onFlippingStart(ParCoolActionEvent.Start.Post event) {
        if (!(event.getAction() instanceof FlipSkill flipSkill)) return;

        var player = event.getPlayer();
        var parkourability = Parkourability.get(player);
        var dodge = parkourability.get(Dodge.class);
        if (!dodge.isDoing()) return;

        var flickFlackStaminaConsumption = Config.FLICK_FLACK_STAMINA_CONSUMPTION.get();
        var stamina = Stamina.get(player);
        stamina.consume(flickFlackStaminaConsumption);
        if (stamina.isExhausted()) return;

        flipSkill.parcoolskill$setReadyType(FlipSkill.Type.VAULT);
        flipSkill.parcoolskill$setInvulnerableTime(20);

        var movement = player.getDeltaMovement();
        var movementVector = new Vec2((float) movement.x, (float) movement.z);
        var movementDirection = movementVector.normalized().scale(0.5F);
        player.setDeltaMovement(movementDirection.x, movement.y, movementDirection.y);
    }

    @SubscribeEvent
    public static void onFlippingFinish(ParCoolActionEvent.Finish.Pre event) {
        if (!(event.getAction() instanceof FlipSkill flipSkill)) return;

        flipSkill.parcoolskill$setReadyType(FlipSkill.Type.NONE);
    }

    @SubscribeEvent
    public static void onSlideTick(ParCoolActionEvent.Tick.Pre event) {
        if (!(event.getAction() instanceof Flipping flipping)) return;

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

    public static void onFlippingVault(Player player) {
        var movement = player.getDeltaMovement();
        if (movement.y < 0.0) return;
        player.setDeltaMovement(movement.x, movement.y * 0.5 + 0.45, movement.z);
        SkillHandler.use(SkillPayload.Type.FLIPPING_VAULT, player);

        if (!ParCoolConfig.Client.Booleans.EnableActionSounds.get()) return;
        player.playSound(SoundEvents.VAULT.get(), 1.0F, 1.0F);
    }

    @SubscribeEvent
    public static void onPlayerAttack(PlayerAttackEvent.Pre event) {
        var player = event.getEntity();
        if (!isReadyForAttack(player)) return;

        var flickFlackNeutralizedDuration = Config.FLICK_FLACK_NEUTRALIZED_DURATION.get();
        var target = event.getTarget();
        SkillHandler.addEffect(target, player, Effects.NEUTRALIZED, flickFlackNeutralizedDuration);

        player.sweepAttack();
        SkillParticleHandler.emit(SkillParticlePayload.Type.SILENT_HIT, target);

        for (var living : target.level().getEntitiesOfClass(LivingEntity.class, getSweepHitBox(target))) {
            var entityReachSquare = Mth.square(player.entityInteractionRange());
            if (living != player && living != target && !player.isAlliedTo(living) && (!(living instanceof ArmorStand) || !((ArmorStand) living).isMarker()) && player.distanceToSqr(living) < entityReachSquare) {
                player.attackStrengthTicker = (int) player.getCurrentItemAttackStrengthDelay();
                player.attack(living);
                SkillHandler.addEffect(living, player, Effects.NEUTRALIZED, flickFlackNeutralizedDuration);

                SkillParticleHandler.emit(SkillParticlePayload.Type.SILENT_HIT, living);
            }
        }

        var movement = player.getDeltaMovement();
        player.setDeltaMovement(movement.x, movement.y - 0.2, movement.z);
    }

    public static void handleVault(Player player) {
        var flipSkill = (FlipSkill) Parkourability.get(player).get(Flipping.class);
        if (flipSkill.parcoolskill$getReadyType() != FlipSkill.Type.VAULT) return;

        flipSkill.parcoolskill$setReadyType(FlipSkill.Type.ATTACK);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isReadyForVault(Player player) {
        var flipSkill = (FlipSkill) Parkourability.get(player).get(Flipping.class);
        if (flipSkill.parcoolskill$getReadyType() != FlipSkill.Type.VAULT) return false;

        flipSkill.parcoolskill$setReadyType(FlipSkill.Type.ATTACK);
        return true;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isReadyForAttack(Player player) {
        var flipSkill = (FlipSkill) Parkourability.get(player).get(Flipping.class);
        if (flipSkill.parcoolskill$getReadyType() != FlipSkill.Type.ATTACK) return false;

        flipSkill.parcoolskill$setReadyType(FlipSkill.Type.NONE);
        return true;
    }

    private static AABB getSweepHitBox(Entity target) {
        return target.getBoundingBox().inflate(1.0, 0.25, 1.0);
    }
}
