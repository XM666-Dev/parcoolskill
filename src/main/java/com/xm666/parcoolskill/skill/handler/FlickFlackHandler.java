package com.xm666.parcoolskill.skill.handler;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import com.alrex.parcool.common.action.impl.Dodge;
import com.alrex.parcool.common.action.impl.Flipping;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.xm666.parcoolskill.Config;
import com.xm666.parcoolskill.ParCoolSkill;
import com.xm666.parcoolskill.animation.ArmAnimation;
import com.xm666.parcoolskill.effect.Effects;
import com.xm666.parcoolskill.event.PlayerAttackEvent;
import com.xm666.parcoolskill.handler.SkillHandler;
import com.xm666.parcoolskill.handler.StaminaHandler;
import com.xm666.parcoolskill.network.SkillParticlePayload;
import com.xm666.parcoolskill.particle.SkillParticleHandler;
import com.xm666.parcoolskill.skill.FlippingSkill;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;
import org.joml.Vector3f;

import java.util.ArrayDeque;
import java.util.stream.Collectors;

@EventBusSubscriber(modid = ParCoolSkill.MODID)
public class FlickFlackHandler {
    public static final ArmAnimation ARM_ANIMATION = new ArmAnimation(
            new Vector3f(-0.25F, 0.35F, 0.05F),
            new Vector3f(-55.0F, 35.3F, -9.785F),
            0.2F,
            false
    );
    private static final ResourceLocation MOVEMENT_SPEED_MODIFIER = ResourceLocation.fromNamespaceAndPath(
            ParCoolSkill.MODID, "modifier.movement_speed.flick_flack"
    );
    private static ArrayDeque<LivingEntity> targets;

    @SubscribeEvent
    public static void onFlippingStart(ParCoolActionEvent.Start.Post event) {
        if (!Config.FLICK_FLACK_ENABLED.get() || !(event.getAction() instanceof FlippingSkill flippingSkill)) return;

        var player = event.getPlayer();
        var parkourability = Parkourability.get(player);
        var dodge = parkourability.get(Dodge.class);
        if (!dodge.isDoing()) return;

        flippingSkill.parcoolskill$setAttackReady(true);
        flippingSkill.parcoolskill$setAccelerated(true);

        var movement = player.getDeltaMovement();
        player.setDeltaMovement(movement.x, movement.y * 1.625, movement.z);

        if (!player.level().isClientSide() || !ParCoolConfig.Client.Booleans.EnableActionSounds.get()) return;
        player.playSound(SoundEvents.VAULT.get(), 1.0F, 1.0F);
    }

    @SubscribeEvent
    public static void onFlippingFinish(ParCoolActionEvent.Finish.Pre event) {
        if (!(event.getAction() instanceof FlippingSkill flippingSkill)) return;

        flippingSkill.parcoolskill$setAttackReady(false);
        flippingSkill.parcoolskill$setAccelerated(false);
    }

    @SubscribeEvent
    public static void onFlippingTick(ParCoolActionEvent.Tick.Pre event) {
        if (!(event.getAction() instanceof FlippingSkill flippingSkill)) return;

        var player = event.getPlayer();
        var movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed != null) {
            if (movementSpeed.getModifier(MOVEMENT_SPEED_MODIFIER) != null) {
                movementSpeed.removeModifier(MOVEMENT_SPEED_MODIFIER);
            }
            if (flippingSkill.parcoolskill$isAccelerated()) {
                var flickFlackSpeedMultiplierAddition = Config.FLICK_FLACK_SPEED_MULTIPLIER_ADDITION.get();
                movementSpeed.addTransientModifier(new AttributeModifier(
                        MOVEMENT_SPEED_MODIFIER,
                        flickFlackSpeedMultiplierAddition,
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                ));
            }
        }

        var parryTime = flippingSkill.parcoolskill$getParryTime();
        if (parryTime > 0) {
            flippingSkill.parcoolskill$setParryTime(parryTime - 1);
        }
    }

    @SubscribeEvent
    public static void onPlayerAttack(PlayerAttackEvent.Pre event) {
        var player = event.getEntity();
        if (attackTarget(player, event) || !isAttackReady(player) || !event.isFullStrength()) return;

        var flickFlackStaminaConsumption = Config.FLICK_FLACK_STAMINA_CONSUMPTION.get();
        StaminaHandler.consume(player, flickFlackStaminaConsumption);

        var flickFlackNeutralizedDuration = Config.FLICK_FLACK_NEUTRALIZED_DURATION.get();
        var target = event.getTarget();
        if (target instanceof LivingEntity living) {
            SkillHandler.addEffect(living, player, Effects.NEUTRALIZED, flickFlackNeutralizedDuration);
        }

        var flickFlackParryDuration = Config.FLICK_FLACK_PARRY_DURATION.get();
        var parkourability = Parkourability.get(player);
        var flippingSkill = (FlippingSkill) parkourability.get(Flipping.class);
        flippingSkill.parcoolskill$setParryTime(flickFlackParryDuration);

        event.setDisableCrit(true);
        SkillParticleHandler.emit(SkillParticlePayload.Type.SILENT_HIT, target);
        player.sweepAttack();

        if (player.isLocalPlayer()) return;

        var weapon = player.getWeaponItem();
        var hitBox = weapon.getSweepHitBox(player, target);
        targets = target.level().getEntitiesOfClass(LivingEntity.class, hitBox).stream()
                .filter(living -> canSweep(player, target, living))
                .collect(Collectors.toCollection(ArrayDeque::new));
        attackTarget(player, event);
    }

    @SubscribeEvent
    public static void onLivingBlock(LivingShieldBlockEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        var parkourability = Parkourability.get(player);
        var flippingSkill = (FlippingSkill) parkourability.get(Flipping.class);
        if (flippingSkill.parcoolskill$getParryTime() == 0
                || !SkillHandler.isDamageSourceBlocked(player, event.getDamageSource())) return;

        event.setBlocked(true);
    }

    public static boolean canJump(Parkourability parkourability) {
        var mc = Minecraft.getInstance();
        var control = ParCoolConfig.Client.getInstance().FlipControl.get();
        var flippingSkill = (FlippingSkill) parkourability.get(Flipping.class);
        return mc.hitResult instanceof EntityHitResult && control.isInputDone(flippingSkill.parcoolskill$justJumped());
    }

    private static boolean isAttackReady(Player player) {
        var parkourability = Parkourability.get(player);
        var flippingSkill = (FlippingSkill) parkourability.get(Flipping.class);
        if (!flippingSkill.parcoolskill$isAttackReady()) return false;

        flippingSkill.parcoolskill$setAttackReady(false);
        return true;
    }

    private static AABB getSweepHitBox(Entity target) {
        return target.getBoundingBox().inflate(1.0, 0.25, 1.0);
    }

    private static boolean canSweep(Player player, Entity target, LivingEntity living) {
        return living != player
                && living != target
                && !player.isAlliedTo(living)
                && (!(living instanceof ArmorStand armorStand) || !armorStand.isMarker())
                && player.distanceToSqr(living) < Mth.square(player.entityInteractionRange());
    }

    private static boolean attackTarget(Player player, PlayerAttackEvent.Pre event) {
        if (player.isLocalPlayer() || targets == null || targets.isEmpty()) return false;

        var flickFlackNeutralizedDuration = Config.FLICK_FLACK_NEUTRALIZED_DURATION.get();
        var target = targets.pop();
        player.attackStrengthTicker = (int) player.getCurrentItemAttackStrengthDelay();
        player.attack(target);
        SkillHandler.addEffect(target, player, Effects.NEUTRALIZED, flickFlackNeutralizedDuration);

        event.setDisableCrit(true);
        SkillParticleHandler.emit(SkillParticlePayload.Type.SILENT_HIT, target);
        return true;
    }
}
