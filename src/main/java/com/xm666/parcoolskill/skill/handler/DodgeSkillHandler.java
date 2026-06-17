package com.xm666.parcoolskill.skill.handler;

import com.alrex.parcool.api.Stamina;
import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import com.alrex.parcool.common.action.impl.CatLeap;
import com.alrex.parcool.common.action.impl.ChargeJump;
import com.alrex.parcool.common.action.impl.Dodge;
import com.alrex.parcool.common.action.impl.Flipping;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.xm666.parcoolskill.Config;
import com.xm666.parcoolskill.ParCoolSkill;
import com.xm666.parcoolskill.event.PlayerAttackEvent;
import com.xm666.parcoolskill.network.SkillParticlePayload;
import com.xm666.parcoolskill.particle.SkillParticleHandler;
import com.xm666.parcoolskill.skill.DodgeSkill;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.joml.Vector2d;

@EventBusSubscriber(modid = ParCoolSkill.MODID)
public class DodgeSkillHandler {
    @SubscribeEvent
    public static void onDodgeStart(ParCoolActionEvent.Start.Pre event) {
        if (!(event.getAction() instanceof DodgeSkill dodgeSkill)) return;

        dodgeSkill.parcoolskill$setAttackReady(true);
    }

    @SubscribeEvent
    public static void onDodgeFinish(ParCoolActionEvent.Finish.Pre event) {
        if (!(event.getAction() instanceof DodgeSkill dodgeSkill)) return;

        dodgeSkill.parcoolskill$setAttackReady(false);
    }

    @SubscribeEvent
    public static void onDodgeTick(ParCoolActionEvent.Tick.Pre event) {
        if (!(event.getAction() instanceof DodgeSkill dodgeSkill)) return;

        var attackReadyTime = dodgeSkill.parcoolskill$getAttackReadyTime();
        if (attackReadyTime == 0) return;

        dodgeSkill.parcoolskill$setAttackReadyTime(attackReadyTime - 1);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        if (!Config.SNEAKY_STRIKE_ENABLED.get() || !(event.getEntity() instanceof Player player) || event.getSource().is(DamageTypeTags.BYPASSES_ARMOR))
            return;

        var parkourability = Parkourability.get(player);
        if (!parkourability.getServerLimitation().get(ParCoolConfig.Server.Booleans.DodgeProvideInvulnerableFrame))
            return;

        var dodge = parkourability.get(Dodge.class);
        if (!dodge.isDoing() || dodge.getDoingTick() > 10) return;

        var sneakyStrikeReadyDuration = Config.SNEAKY_STRIKE_READY_DURATION.get();
        var dodgeSkill = (DodgeSkill) dodge;
        dodgeSkill.parcoolskill$setAttackReadyTime(sneakyStrikeReadyDuration);
    }

    @SubscribeEvent
    public static void onPlayerAttack(PlayerAttackEvent.Pre event) {
        var player = event.getEntity();
        var parkourability = Parkourability.get(player);
        var dodgeSkill = (DodgeSkill) parkourability.get(Dodge.class);
        dodgeSkill.parcoolskill$setAttackReady(false);

        if (dodgeSkill.parcoolskill$getAttackReadyTime() == 0) return;
        dodgeSkill.parcoolskill$setAttackReadyTime(0);

        if (parkourability.get(CatLeap.class).isDoing()
                || parkourability.get(ChargeJump.class).isDoing()
                || parkourability.get(Flipping.class).isDoing()
                || CleaveHandler.isAttackReady(player)
                || CleaveHandler.isAttacking(player)
                || !event.isFullStrength())
            return;

        var sneakyStrikeStaminaConsumption = Config.SNEAKY_STRIKE_STAMINA_CONSUMPTION.get();
        var stamina = Stamina.get(player);
        if (stamina.getValue() < sneakyStrikeStaminaConsumption) return;

        var behind = false;
        var target = event.getTarget();
        if (target instanceof LivingEntity living) {
            var sourcePosition = player.getEyePosition();
            var targetPosition = target.position();
            var sourceOffset = sourcePosition.subtract(targetPosition);
            var targetDirection = directionFromBodyRotation(living.yBodyRot);
            var offset = new Vector2d(sourceOffset.x, sourceOffset.z);
            var direction = new Vector2d(targetDirection.x, targetDirection.z);
            behind = isPositionBehind(offset, direction);
        }

        var sneakyStrikeDamageMultiplier = Config.SNEAKY_STRIKE_DAMAGE_MULTIPLIER.get().floatValue();
        var sneakyStrikeBackstabDamageMultiplier = Config.SNEAKY_STRIKE_BACKSTAB_DAMAGE_MULTIPLIER.get().floatValue();
        var damageMultiplier = behind ? sneakyStrikeBackstabDamageMultiplier : sneakyStrikeDamageMultiplier;
        event.setCriticalHit(true);
        event.setDamageMultiplier(event.getDamageMultiplier() * damageMultiplier);
        event.setDisableSweep(false);

        event.setDisableCrit(true);
        SkillParticleHandler.emit(SkillParticlePayload.Type.SILENT_HIT, target);
    }

    private static Vec3 directionFromBodyRotation(float rotation) {
        var radian = rotation * Mth.DEG_TO_RAD;
        var x = -Mth.sin(radian);
        var z = Mth.cos(radian);
        return new Vec3(x, 0.0, z);
    }

    private static boolean isPositionBehind(Vector2d position, Vector2d direction) {
        return position.dot(direction) <= 0.0;
    }
}
