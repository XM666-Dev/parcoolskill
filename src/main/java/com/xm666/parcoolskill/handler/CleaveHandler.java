package com.xm666.parcoolskill.handler;

import com.alrex.parcool.api.Stamina;
import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import com.alrex.parcool.common.action.impl.ChargeJump;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.xm666.parcoolskill.ParCoolSkill;
import com.xm666.parcoolskill.skill.JumpSkill;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Predicate;

@EventBusSubscriber(modid = ParCoolSkill.MODID)
public class CleaveHandler {
    public static HashSet<Entity> entitiesHit = new HashSet<>();

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

        if (jump.getChargingTick() < ChargeJump.JUMP_ANIMATION_TICK) return;
        ChargeCooldownHandler.cooldown = true;

        var stamina = Stamina.get(player);
        if (stamina.getValue() < 200) return;
        stamina.consume(200);

        jumpSkill.parcoolskill$setAttackTime(10);
        entitiesHit.clear();
        BulletTimeHandler.addScale(0.25F, 20);
        event.setCanceled(true);
    }

    public static void handleAttack(LivingEntity target, Player player) {
        player.attackStrengthTicker = (int) player.getCurrentItemAttackStrengthDelay();
        player.attack(target);
    }

    public static Entity[] getEntitiesHit(Entity shooter, Vec3 startVec, Vec3 endVec, AABB boundingBox, Predicate<Entity> filter, float inflationAmount) {
        var level = shooter.level();
        var entities = new ArrayList<Entity>();

        for (Entity entity : level.getEntities(shooter, boundingBox, filter)) {
            var aabb = entity.getBoundingBox().inflate(entity.getPickRadius() + inflationAmount);
            var optionalPoint = aabb.clip(startVec, endVec);
            if (aabb.contains(startVec) || optionalPoint.isPresent()) {
                entities.add(entity);
            }
        }

        return entities.toArray(Entity[]::new);
    }
}
