package com.xm666.parcoolskill.handler;

import com.alrex.parcool.api.Stamina;
import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import com.alrex.parcool.common.action.impl.ChargeJump;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.xm666.parcoolskill.Config;
import com.xm666.parcoolskill.ParCoolSkill;
import com.xm666.parcoolskill.skill.JumpSkill;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.common.ItemAbilities;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.function.Predicate;

@EventBusSubscriber(modid = ParCoolSkill.MODID)
public class CleaveHandler {
    public static HashSet<Entity> entityHits = new HashSet<>();

    @SubscribeEvent
    static void onJumpTick(ParCoolActionEvent.Tick.Pre event) {
        if (!(event.getAction() instanceof JumpSkill jumpSkill)) return;

        var attackTime = jumpSkill.parcoolskill$getAttackTime();
        if (attackTime == 0) return;

        jumpSkill.parcoolskill$setAttackTime(attackTime - 1);
    }

    @SubscribeEvent
    static void onClickInput(InputEvent.InteractionKeyMappingTriggered event) {
        if (!event.isAttack()) return;

        var mc = Minecraft.getInstance();
        var player = mc.player;

        var jump = Parkourability.get(player).get(ChargeJump.class);
        var jumpSkill = (JumpSkill) jump;
        if (jumpSkill.parcoolskill$getAttackTime() > 0) {
            event.setCanceled(true);
            return;
        }

        var cleaveChargeDuration = Config.CLEAVE_CHARGE_DURATION.get();
        if (!player.getWeaponItem().canPerformAction(ItemAbilities.SWORD_SWEEP) || jump.getChargingTick() < cleaveChargeDuration)
            return;
        ChargeCooldownHandler.cooldown = true;

        var cleaveStaminaConsumption = Config.CLEAVE_STAMINA_CONSUMPTION.get();
        var stamina = Stamina.get(player);
        stamina.consume(cleaveStaminaConsumption);
        if (stamina.isExhausted()) return;

        var cleaveAttackDuration = Config.CLEAVE_ATTACK_DURATION.get();
        var cleaveBulletTimeScale = Config.CLEAVE_BULLET_TIME_SCALE.get().floatValue();
        var cleaveBulletTimeDuration = Config.CLEAVE_BULLET_TIME_DURATION.get();
        jumpSkill.parcoolskill$setAttackTime(cleaveAttackDuration);
        TimeScaleHandler.applyScale(cleaveBulletTimeScale, cleaveBulletTimeDuration);
        entityHits.clear();
        event.setCanceled(true);
    }

    public static void handleAttack(Entity target, Player player) {
        player.attackStrengthTicker = (int) player.getCurrentItemAttackStrengthDelay();
        player.attack(target);
    }

    public static Entity[] getEntityHits(Entity shooter, Vec3 startPosition, Vec3 endPosition, AABB boundingBox, Predicate<Entity> filter, double inflationAmount, long hitLimit) {
        record HitResult(Entity entity, double distanceSquare) {
        }

        var level = shooter.level();
        var hitResults = new ArrayList<HitResult>();

        for (Entity entity : level.getEntities(shooter, boundingBox, filter)) {
            var aabb = entity.getBoundingBox().inflate(entity.getPickRadius() + inflationAmount);
            var optionalPoint = aabb.clip(startPosition, endPosition);
            double distanceSquare;
            if (aabb.contains(startPosition)) {
                distanceSquare = 0.0;
            } else if (optionalPoint.isPresent()) {
                var point = optionalPoint.get();
                distanceSquare = startPosition.distanceToSqr(point);
            } else {
                continue;
            }
            hitResults.add(new HitResult(entity, distanceSquare));
        }

        return hitResults.stream()
                .sorted(Comparator.comparingDouble((HitResult hitResult) -> hitResult.distanceSquare))
                .map(hitResult -> hitResult.entity)
                .limit(hitLimit)
                .toArray(Entity[]::new);
    }
}
