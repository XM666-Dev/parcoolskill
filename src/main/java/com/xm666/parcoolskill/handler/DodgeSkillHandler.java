package com.xm666.parcoolskill.handler;

import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import com.alrex.parcool.common.action.impl.Dodge;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.xm666.parcoolskill.Config;
import com.xm666.parcoolskill.ParCoolSkill;
import com.xm666.parcoolskill.event.PlayerAttackEvent;
import com.xm666.parcoolskill.network.SkillParticlePayload;
import com.xm666.parcoolskill.skill.DodgeSkill;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

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
        if (!(event.getEntity() instanceof Player player) || event.getSource().is(DamageTypeTags.BYPASSES_ARMOR))
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
        var dodgeSkill = (DodgeSkill) Parkourability.get(player).get(Dodge.class);
        dodgeSkill.parcoolskill$setAttackReady(false);

        if (dodgeSkill.parcoolskill$getAttackReadyTime() == 0) return;
        dodgeSkill.parcoolskill$setAttackReadyTime(0);

        if (!event.isFullStrength()) return;

        var target = event.getTarget();
        var sourcePosition = player.getEyePosition();
        var targetPosition = target.position();
        var sourceOffset = sourcePosition.subtract(targetPosition);
        var targetDirection = directionFromBodyRotation(target.yBodyRot);
        var offset = new Vec2((float) sourceOffset.x, (float) sourceOffset.z);
        var direction = new Vec2((float) targetDirection.x, (float) targetDirection.z);

        var sneakyStrikeDamageMultiplier = Config.SNEAKY_STRIKE_DAMAGE_MULTIPLIER.get().floatValue();
        var backstabDamageMultiplier = Config.BACKSTAB_DAMAGE_MULTIPLIER.get().floatValue();
        var damageMultiplier = isPositionBehind(offset, direction) ? backstabDamageMultiplier : sneakyStrikeDamageMultiplier;
        event.setAmount(event.getAmount() * damageMultiplier);
        StaminaHandler.recoverStaminaOf(player, Dodge.class);

        SkillParticleHandler.emit(SkillParticlePayload.Type.SILENT_HIT, target);
    }

    static Vec3 directionFromBodyRotation(float rotation) {
        var radian = rotation * Mth.DEG_TO_RAD;

        var x = -Mth.sin(radian);
        var z = Mth.cos(radian);

        return new Vec3(x, 0.0, z);
    }

    static boolean isPositionBehind(Vec2 position, Vec2 direction) {
        return position.dot(direction) <= 0;
    }
}
