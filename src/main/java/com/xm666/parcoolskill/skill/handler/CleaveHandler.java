package com.xm666.parcoolskill.skill.handler;

import com.alrex.parcool.api.Stamina;
import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import com.alrex.parcool.common.action.impl.ChargeJump;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.xm666.parcoolskill.Config;
import com.xm666.parcoolskill.ParCoolSkill;
import com.xm666.parcoolskill.animation.ArmAnimation;
import com.xm666.parcoolskill.client.CleaveClientHandler;
import com.xm666.parcoolskill.event.PlayerAttackEvent;
import com.xm666.parcoolskill.handler.StaminaHandler;
import com.xm666.parcoolskill.network.SkillParticlePayload;
import com.xm666.parcoolskill.particle.SkillParticleHandler;
import com.xm666.parcoolskill.skill.JumpSkill;
import com.xm666.timescalelib.handler.TimeScaleHandler;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.SweepAttackEvent;
import org.joml.Vector3f;

@Mod(ParCoolSkill.MODID)
@EventBusSubscriber(modid = ParCoolSkill.MODID)
public class CleaveHandler {
    public static final ArmAnimation ARM_ANIMATION = new ArmAnimation(
            new Vector3f(-0.1392841F, 0.091721935F, 0.078657655F),
            new Vector3f(-13.935F, 35.3F, -9.785F),
            0.04F,
            true
    );
    private static final Identifier ENTITY_INTERACTION_RANGE_MODIFIER = Identifier.fromNamespaceAndPath(
            ParCoolSkill.MODID, "modifier.entity_interaction_range.cleave"
    );
    public static int animationTick;

    public CleaveHandler() {
        if (FMLEnvironment.getDist() != Dist.CLIENT) return;

        NeoForge.EVENT_BUS.addListener(CleaveClientHandler::onClickInput);
    }

    @SubscribeEvent
    public static void onJumpTick(ParCoolActionEvent.Tick.Pre event) {
        if (!(event.getAction() instanceof ChargeJump jump)) return;

        var player = event.getPlayer();
        var entityInteractionRange = player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
        if (entityInteractionRange != null) {
            if (entityInteractionRange.getModifier(ENTITY_INTERACTION_RANGE_MODIFIER) != null) {
                entityInteractionRange.removeModifier(ENTITY_INTERACTION_RANGE_MODIFIER);
            }
            if (isAttackReady(player) || isAttacking(player)) {
                var cleaveRangeMultiplierAddition = Config.CLEAVE_RANGE_MULTIPLIER_ADDITION.get();
                entityInteractionRange.addTransientModifier(new AttributeModifier(
                        ENTITY_INTERACTION_RANGE_MODIFIER,
                        cleaveRangeMultiplierAddition,
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                ));
            }
        }

        var jumpSkill = (JumpSkill) jump;
        var attackTime = jumpSkill.parcoolskill$getAttackTime();
        if (attackTime == 0) return;

        jumpSkill.parcoolskill$setAttackTime(attackTime - 1);
    }

    @SubscribeEvent
    public static void onPlayerAttack(PlayerAttackEvent.Pre event) {
        var player = event.getEntity();
        if (!isAttacking(player)) return;

        var sweepingDamageRatio = (float) player.getAttributeValue(Attributes.SWEEPING_DAMAGE_RATIO);
        if (sweepingDamageRatio == 0.0F) return;

        event.setCriticalHit(true);
        event.setDamageMultiplier(event.getDamageMultiplier() * (1.0F + sweepingDamageRatio));
        event.setDisableCrit(true);
    }

    @SubscribeEvent
    public static void onPlayerAttack(SweepAttackEvent event) {
        var player = event.getEntity();
        if (!isAttacking(player)) return;

        event.setCanceled(true);
    }

    public static void handleReady(Player player) {
        var cleaveStaminaConsumption = Config.CLEAVE_STAMINA_CONSUMPTION.get();
        StaminaHandler.consume(player, cleaveStaminaConsumption);

        var cleaveAttackDuration = Config.CLEAVE_ATTACK_DURATION.get();
        var cleaveBulletTimeScale = Config.CLEAVE_BULLET_TIME_SCALE.get().floatValue();
        var cleaveBulletTimeDuration = Config.CLEAVE_BULLET_TIME_DURATION.get();
        var parkourability = Parkourability.get(player);
        var jump = parkourability.get(ChargeJump.class);
        var jumpSkill = (JumpSkill) jump;
        jumpSkill.parcoolskill$setAttackTime(cleaveAttackDuration);
        jumpSkill.parcoolskill$clearEntityHits();
        TimeScaleHandler.applyScale(player, cleaveBulletTimeScale, cleaveBulletTimeDuration);
    }

    public static void handleAttack(Player player, Entity target) {
        if (!isAttacking(player)) return;

        var boundingBox = target.getBoundingBox();
        if (!player.isWithinEntityInteractionRange(boundingBox, 1.0)) return;

        var parkourability = Parkourability.get(player);
        var jumpSkill = (JumpSkill) parkourability.get(ChargeJump.class);
        if (!jumpSkill.parcoolskill$addEntityHit(target)) return;

        player.attackStrengthTicker = (int) player.getCurrentItemAttackStrengthDelay();
        player.attack(target);

        SkillParticleHandler.emit(SkillParticlePayload.Type.IRONCLAD_HIT, target);
    }

    public static boolean isAttacking(Player player) {
        if (!canUseCleave(player)) return false;

        var parkourability = Parkourability.get(player);
        var jumpSkill = (JumpSkill) parkourability.get(ChargeJump.class);
        return jumpSkill.parcoolskill$getAttackTime() > 0;
    }

    public static boolean isCharging(Player player) {
        if (!canUseCleave(player)) return false;

        var parkourability = Parkourability.get(player);
        var jump = parkourability.get(ChargeJump.class);
        if (jump.getNotChargingTick() > 0) return false;

        var stamina = Stamina.get(player);
        return !stamina.isExhausted();
    }

    public static int getHitCount(Player player) {
        var cleaveHitCountBase = Config.CLEAVE_HIT_COUNT_BASE.get();
        var registryAccess = player.level().registryAccess();
        var sweepingEdge = registryAccess.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SWEEPING_EDGE);
        return cleaveHitCountBase + player.getWeaponItem().getEnchantmentLevel(sweepingEdge);
    }

    public static boolean isAttackReady(Player player) {
        if (!isCharging(player)) return false;

        var cleaveChargeDuration = Config.CLEAVE_CHARGE_DURATION.get();
        var parkourability = Parkourability.get(player);
        var jump = parkourability.get(ChargeJump.class);
        if (jump.getChargingTick() < cleaveChargeDuration) return false;

        var attackStrengthScale = player.getAttackStrengthScale(0.5F);
        return attackStrengthScale > 0.9F;
    }

    private static boolean canUseCleave(Player player) {
        if (!Config.CLEAVE_ENABLED.get()) return false;

        var weapon = player.getWeaponItem();
        return weapon.canPerformAction(ItemAbilities.SWORD_SWEEP);
    }
}
