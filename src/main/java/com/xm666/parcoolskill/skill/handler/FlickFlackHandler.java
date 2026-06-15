package com.xm666.parcoolskill.skill.handler;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import com.alrex.parcool.common.action.impl.Dodge;
import com.alrex.parcool.common.action.impl.Flipping;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.xm666.parcoolskill.Config;
import com.xm666.parcoolskill.ParCoolSkill;
import com.xm666.parcoolskill.animation.HandAnimation;
import com.xm666.parcoolskill.effect.Effects;
import com.xm666.parcoolskill.event.PlayerAttackEvent;
import com.xm666.parcoolskill.handler.SkillHandler;
import com.xm666.parcoolskill.handler.StaminaHandler;
import com.xm666.parcoolskill.network.SkillParticlePayload;
import com.xm666.parcoolskill.particle.SkillParticleHandler;
import com.xm666.parcoolskill.skill.FlippingSkill;
import net.minecraft.client.Minecraft;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.joml.Vector3f;

@EventBusSubscriber(modid = ParCoolSkill.MODID)
public class FlickFlackHandler {
    public static final HandAnimation HAND_ANIMATION = new HandAnimation(
            new Vector3f(-0.25F, 0.35F, 0.05F),
            new Vector3f(-55.0F, 35.3F, -9.785F),
            0.2F,
            false
    );
    private static boolean disableCrit;

    @SubscribeEvent
    public static void onFlippingStart(ParCoolActionEvent.Start.Post event) {
        if (!Config.FLICK_FLACK_ENABLED.get() || !(event.getAction() instanceof FlippingSkill flippingSkill)) return;

        var player = event.getPlayer();
        var parkourability = Parkourability.get(player);
        var dodge = parkourability.get(Dodge.class);
        if (!dodge.isDoing()) return;

        var flickFlackInvulnerableDuration = Config.FLICK_FLACK_INVULNERABLE_DURATION.get();
        flippingSkill.parcoolskill$setAttackReady(true);
        flippingSkill.parcoolskill$setInvulnerableTime(flickFlackInvulnerableDuration);

        var movement = player.getDeltaMovement();
        player.setDeltaMovement(movement.x, movement.y * 1.625, movement.z);

        if (!player.level().isClientSide() || !ParCoolConfig.Client.Booleans.EnableActionSounds.get()) return;
        player.playSound(SoundEvents.VAULT.get(), 1.0F, 1.0F);
    }

    @SubscribeEvent
    public static void onFlippingFinish(ParCoolActionEvent.Finish.Pre event) {
        if (!(event.getAction() instanceof FlippingSkill flippingSkill)) return;

        flippingSkill.parcoolskill$setAttackReady(false);
    }

    @SubscribeEvent
    public static void onFlippingTick(ParCoolActionEvent.Tick.Pre event) {
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
        if (!isAttackReady(player)) {
            if (disableCrit) {
                event.setDisableCrit(true);
            }
            return;
        }

        if (!event.isFullStrength()) return;

        var flickFlackStaminaConsumption = Config.FLICK_FLACK_STAMINA_CONSUMPTION.get();
        StaminaHandler.consume(player, flickFlackStaminaConsumption);

        var flickFlackNeutralizedDuration = Config.FLICK_FLACK_NEUTRALIZED_DURATION.get();
        var target = event.getTarget();
        if (target instanceof LivingEntity living) {
            SkillHandler.addEffect(living, player, Effects.NEUTRALIZED, flickFlackNeutralizedDuration);
        }

        event.setDisableCrit(true);
        SkillParticleHandler.emit(SkillParticlePayload.Type.SILENT_HIT, target);
        player.sweepAttack();

        disableCrit = true;
        for (var living : target.level().getEntitiesOfClass(LivingEntity.class, getSweepHitBox(target))) {
            var entityReachSquare = Mth.square(player.entityInteractionRange());
            if (living != player && living != target && !player.isAlliedTo(living) && (!(living instanceof ArmorStand) || !((ArmorStand) living).isMarker()) && player.distanceToSqr(living) < entityReachSquare) {
                player.attackStrengthTicker = (int) player.getCurrentItemAttackStrengthDelay();
                player.attack(living);
                SkillHandler.addEffect(living, player, Effects.NEUTRALIZED, flickFlackNeutralizedDuration);
                SkillParticleHandler.emit(SkillParticlePayload.Type.SILENT_HIT, living);
            }
        }
        disableCrit = false;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean isAttackReady(Player player) {
        var flippingSkill = (FlippingSkill) Parkourability.get(player).get(Flipping.class);
        if (!flippingSkill.parcoolskill$isAttackReady()) return false;

        flippingSkill.parcoolskill$setAttackReady(false);
        return true;
    }

    private static AABB getSweepHitBox(Entity target) {
        return target.getBoundingBox().inflate(1.0, 0.25, 1.0);
    }

    public static boolean canJump(Parkourability parkourability) {
        var mc = Minecraft.getInstance();
        var control = ParCoolConfig.Client.getInstance().FlipControl.get();
        var flippingSkill = (FlippingSkill) parkourability.get(Flipping.class);
        return mc.hitResult instanceof EntityHitResult && control.isInputDone(flippingSkill.parcoolskill$justJumped());
    }
}
