package com.xm666.parcoolskill.handler;

import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import com.alrex.parcool.common.action.impl.Flipping;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.xm666.parcoolskill.Config;
import com.xm666.parcoolskill.ParCoolSkill;
import com.xm666.parcoolskill.skill.FlippingSkill;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.phys.Vec2;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

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
        if (cooldown == 0) return;

        flippingSkill.parcoolskill$setCooldown(cooldown - 1);
    }

    @SubscribeEvent
    public static void onUseItemTick(LivingEntityUseItemEvent.Tick event) {
        if (!(event.getEntity() instanceof Player player)) return;

        var flippingSkill = (FlippingSkill) Parkourability.get(player).get(Flipping.class);
        if (flippingSkill.parcoolskill$getSkillTime() == 0) return;

        var duration = event.getDuration() - 1;
        event.setDuration(duration);

        if (player.level().isClientSide) return;

        var item = event.getItem();
        var usingTicks = item.getUseDuration(player) - duration;
        if (getPowerForTime(usingTicks, item, player).orElse(0.0F) < 1.0F || releaseUsingItem) return;

        releaseUsingItem = true;
        player.useItemRemaining = duration;
        player.releaseUsingItem();
        releaseUsingItem = false;
    }

    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player) || event.getSource().is(DamageTypeTags.BYPASSES_ARMOR))
            return;

        var flippingSkill = (FlippingSkill) Parkourability.get(player).get(Flipping.class);
        if (flippingSkill.parcoolskill$getSkillTime() == 0) return;

        event.setCanceled(true);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean canStart(ByteBuffer startInfo) {
        return startInfo.getInt(Integer.BYTES * 2) != 0;
    }

    public static void onStart(Player player, FlippingSkill flippingSkill) {
        var backflipStaminaConsumption = Config.BACKFLIP_STAMINA_CONSUMPTION.get();
        StaminaHandler.consume(player, backflipStaminaConsumption);

        var backflipSkillDuration = Config.BACKFLIP_SKILL_DURATION.get();
        var backflipSkillCooldown = Config.BACKFLIP_SKILL_COOLDOWN.get();
        flippingSkill.parcoolskill$setSkillTime(backflipSkillDuration);
        flippingSkill.parcoolskill$setCooldown(backflipSkillCooldown);

        var movement = player.getDeltaMovement();
        var movementVector = new Vec2((float) movement.x, (float) movement.z);
        var movementDirection = movementVector.normalized().scale(0.75F);
        player.setDeltaMovement(movementDirection.x, movement.y * 1.125, movementDirection.y);
    }

    public static Optional<Float> getPowerForTime(int charge, ItemStack stack, LivingEntity shooter) {
        return Optional.ofNullable(
                switch (stack.getItem()) {
                    case BowItem ignored -> BowItem.getPowerForTime(charge);
                    case CrossbowItem ignored -> CrossbowItem.getPowerForTime(charge, stack, shooter);
                    case TridentItem ignored -> charge / 10.0F;
                    default -> null;
                });
    }
}
