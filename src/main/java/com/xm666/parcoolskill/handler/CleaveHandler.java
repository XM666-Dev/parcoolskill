package com.xm666.parcoolskill.handler;

import com.alrex.parcool.api.Stamina;
import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import com.alrex.parcool.common.action.impl.ChargeJump;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.xm666.parcoolskill.Config;
import com.xm666.parcoolskill.ParCoolSkill;
import com.xm666.parcoolskill.network.SkillParticlePayload;
import com.xm666.parcoolskill.network.SkillPayload;
import com.xm666.parcoolskill.skill.JumpSkill;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.common.ItemAbilities;

@EventBusSubscriber(modid = ParCoolSkill.MODID)
public class CleaveHandler {
    @SubscribeEvent
    public static void onJumpTick(ParCoolActionEvent.Tick.Pre event) {
        if (!(event.getAction() instanceof JumpSkill jumpSkill)) return;

        var attackTime = jumpSkill.parcoolskill$getAttackTime();
        if (attackTime == 0) return;

        jumpSkill.parcoolskill$setAttackTime(attackTime - 1);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onClickInput(InputEvent.InteractionKeyMappingTriggered event) {
        if (!event.isAttack()) return;

        var mc = Minecraft.getInstance();
        var player = mc.player;
        if (player == null || !hasCorrectWeapon(player)) return;

        if (isReadyForAttack(player)) {
            event.setCanceled(true);
            return;
        }

        var jump = Parkourability.get(player).get(ChargeJump.class);
        var jumpSkill = (JumpSkill) jump;
        jumpSkill.parcoolskill$setCoolingDown(true);

        var cleaveChargeDuration = Config.CLEAVE_CHARGE_DURATION.get();
        if (jump.getChargingTick() < cleaveChargeDuration) return;

        var cleaveStaminaConsumption = Config.CLEAVE_STAMINA_CONSUMPTION.get();
        var stamina = Stamina.get(player);
        stamina.consume(cleaveStaminaConsumption);
        if (stamina.isExhausted()) return;

        var cleaveAttackDuration = Config.CLEAVE_ATTACK_DURATION.get();
        SkillHandler.use(SkillPayload.Type.CLEAVE_READY, player);
        jumpSkill.parcoolskill$setAttackTime(cleaveAttackDuration);
        jumpSkill.parcoolskill$clearEntityHits();
        event.setCanceled(true);
    }

    public static void handleReady(Player player) {
        var cleaveAttackDuration = Config.CLEAVE_ATTACK_DURATION.get();
        var cleaveBulletTimeScale = Config.CLEAVE_BULLET_TIME_SCALE.get().floatValue();
        var cleaveBulletTimeDuration = Config.CLEAVE_BULLET_TIME_DURATION.get();
        var jump = Parkourability.get(player).get(ChargeJump.class);
        var jumpSkill = (JumpSkill) jump;
        jumpSkill.parcoolskill$setAttackTime(cleaveAttackDuration);
        jumpSkill.parcoolskill$clearEntityHits();
        TimeScaleHandler.applyScale(player, cleaveBulletTimeScale, cleaveBulletTimeDuration);
    }

    public static void handleAttack(Player player, Entity target) {
        if (!isReadyForAttack(player)) return;

        var cleaveHitRangeMultiplier = Config.CLEAVE_HIT_RANGE_MULTIPLIER.get();
        var aabb = target.getBoundingBox();
        var distance = player.entityInteractionRange() * (cleaveHitRangeMultiplier - 1.0) + 1.0;
        if (!player.canInteractWithEntity(aabb, distance)) return;

        var jumpSkill = (JumpSkill) Parkourability.get(player).get(ChargeJump.class);
        if (!jumpSkill.parcoolskill$addEntityHit(target)) return;

        player.attackStrengthTicker = (int) player.getCurrentItemAttackStrengthDelay();
        player.attack(target);

        SkillParticleHandler.emit(SkillParticlePayload.Type.IRONCLAD_HIT, target);
    }

    public static boolean isReadyForAttack(Player player) {
        var jumpSkill = (JumpSkill) Parkourability.get(player).get(ChargeJump.class);
        return jumpSkill.parcoolskill$getAttackTime() > 0 && hasCorrectWeapon(player);
    }

    public static boolean hasCorrectWeapon(Player player) {
        var weapon = player.getWeaponItem();
        return weapon.canPerformAction(ItemAbilities.SWORD_SWEEP);
    }
}
