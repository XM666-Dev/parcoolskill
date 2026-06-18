package com.xm666.parcoolskill.skill.handler;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import com.alrex.parcool.common.action.impl.Dodge;
import com.alrex.parcool.common.action.impl.Flipping;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.xm666.parcoolskill.Config;
import com.xm666.parcoolskill.ParCoolSkill;
import com.xm666.parcoolskill.compact.SpartanWeaponryHandler;
import com.xm666.parcoolskill.handler.StaminaHandler;
import com.xm666.parcoolskill.network.SkillParticlePayload;
import com.xm666.parcoolskill.particle.SkillParticleHandler;
import com.xm666.parcoolskill.skill.FlippingSkill;
import com.xm666.timescalelib.handler.TimeScaleHandler;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.joml.Vector2d;

import java.nio.ByteBuffer;
import java.util.Optional;

@EventBusSubscriber(modid = ParCoolSkill.MODID)
public class BackflipHandler {
    private static boolean releaseUsingItem = false;

    @SubscribeEvent
    public static void onFlippingFinish(ParCoolActionEvent.Finish.Pre event) {
        if (!(event.getAction() instanceof FlippingSkill flippingSkill)) return;

        flippingSkill.parcoolskill$setSkillTime(0);
    }

    @SubscribeEvent
    public static void onFlippingTick(ParCoolActionEvent.Tick.Pre event) {
        if (!(event.getAction() instanceof FlippingSkill flippingSkill)) return;

        var skillTime = flippingSkill.parcoolskill$getSkillTime();
        if (skillTime > 0) {
            flippingSkill.parcoolskill$setSkillTime(skillTime - 1);
        }

        var cooldown = flippingSkill.parcoolskill$getCooldown();
        if (cooldown > 0) {
            flippingSkill.parcoolskill$setCooldown(cooldown - 1);
        }
    }

    @SubscribeEvent
    public static void onUseItemTick(LivingEntityUseItemEvent.Tick event) {
        if (releaseUsingItem || !(event.getEntity() instanceof Player player)) return;

        var parkourability = Parkourability.get(player);
        var flippingSkill = (FlippingSkill) parkourability.get(Flipping.class);
        if (flippingSkill.parcoolskill$getSkillTime() == 0) return;

        var duration = event.getDuration() - 1;
        event.setDuration(duration);

        if (player.level().isClientSide()) return;

        var stack = event.getItem();
        player.useItemRemaining = duration;
        if (!isCharged(stack, player)) return;

        releaseUsingItem = true;
        player.releaseUsingItem();
        releaseUsingItem = false;
    }

    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player) || event.getSource().is(DamageTypeTags.BYPASSES_ARMOR))
            return;

        var parkourability = Parkourability.get(player);
        var flippingSkill = (FlippingSkill) parkourability.get(Flipping.class);
        if (flippingSkill.parcoolskill$getSkillTime() == 0) return;

        event.setCanceled(true);
    }

    public static boolean canStart(FlippingSkill flippingSkill, Parkourability parkourability, Flipping.Direction fDirection) {
        return Config.BACKFLIP_ENABLED.get() && flippingSkill.parcoolskill$getCooldown() == 0 && !parkourability.get(Dodge.class).isDoing() && fDirection == Flipping.Direction.Back;
    }

    public static void tryStart(FlippingSkill flippingSkill, Player player, ByteBuffer startInfo) {
        try {
            if (startInfo.getInt(8) == 0) return;
        } catch (Exception e) {
            return;
        }

        var isLocalPlayer = player.isLocalPlayer();
        var isClientSide = player.level().isClientSide();
        if (isLocalPlayer || !isClientSide) {
            var backflipSkillDuration = Config.BACKFLIP_SKILL_DURATION.get();
            var backflipCooldownDuration = Config.BACKFLIP_COOLDOWN_DURATION.get();
            flippingSkill.parcoolskill$setSkillTime(backflipSkillDuration);
            flippingSkill.parcoolskill$setCooldown(backflipCooldownDuration);
        }

        if (isLocalPlayer) {
            var backflipStaminaConsumption = Config.BACKFLIP_STAMINA_CONSUMPTION.get();
            StaminaHandler.consume(player, backflipStaminaConsumption);

            var movement = player.getDeltaMovement();
            var movementVector = new Vector2d(movement.x, movement.z);
            var movementDirection = movementVector.normalize().mul(0.75);
            player.setDeltaMovement(movementDirection.x, movement.y * 1.125, movementDirection.y);
        }

        if (isClientSide) {
            if (!ParCoolConfig.Client.Booleans.EnableActionSounds.get()) return;

            player.playSound(SoundEvents.CHARGE_JUMP.get(), 1.0F, 1.0F);
        } else {
            var backflipBulletTimeScale = Config.BACKFLIP_BULLET_TIME_SCALE.get().floatValue();
            var backflipBulletTimeDuration = Config.BACKFLIP_BULLET_TIME_DURATION.get();
            TimeScaleHandler.applyScale(player, backflipBulletTimeScale, backflipBulletTimeDuration, 40);

            SkillParticleHandler.emit(SkillParticlePayload.Type.SILENT_EFFECT, player);
        }
    }

    private static boolean isCharged(ItemStack stack, LivingEntity shooter) {
        var power = getPower(stack, shooter);
        return power.isPresent() && power.get() >= 1.0F;
    }

    private static Optional<Float> getPower(ItemStack stack, LivingEntity shooter) {
        if (ModList.get().isLoaded("spartan_weaponry_unofficial")) {
            var result = SpartanWeaponryHandler.getPower(stack, shooter);
            if (result.isPresent()) return result;
        }

        var charge = stack.getUseDuration(shooter) - shooter.getUseItemRemainingTicks();
        return Optional.ofNullable(switch (stack.getItem()) {
            case BowItem ignored -> BowItem.getPowerForTime(charge);
            case CrossbowItem ignored -> CrossbowItem.getPowerForTime(charge, stack, shooter);
            case TridentItem ignored -> charge / 10.0F;
            default -> null;
        });
    }
}
