package com.xm666.parcoolskill.handler;

import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import com.alrex.parcool.common.action.impl.CatLeap;
import com.alrex.parcool.common.action.impl.Flipping;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.xm666.parcoolskill.Config;
import com.xm666.parcoolskill.ParCoolSkill;
import com.xm666.parcoolskill.event.PlayerAttackEvent;
import com.xm666.parcoolskill.network.SkillParticlePayload;
import com.xm666.parcoolskill.skill.LeapSkill;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = ParCoolSkill.MODID)
public class LeapSkillHandler {
    @SubscribeEvent
    public static void onLeapTryToStart(ParCoolActionEvent.TryToStart event) {
        if (!(event.getAction() instanceof CatLeap)) return;

        var player = event.getPlayer();
        var parkourability = Parkourability.get(player);
        if (!parkourability.get(Flipping.class).isDoing()) return;

        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onLeapStart(ParCoolActionEvent.Start.Pre event) {
        if (!(event.getAction() instanceof LeapSkill leapSkill) || !Config.WILD_STRIKE_ENABLED.get()) return;

        leapSkill.parcoolskill$setAttackReady(true);
    }

    @SubscribeEvent
    public static void onLeapFinish(ParCoolActionEvent.Finish.Pre event) {
        if (!(event.getAction() instanceof LeapSkill leapSkill)) return;

        leapSkill.parcoolskill$setAttackReady(false);
    }

    @SubscribeEvent
    public static void onLeapTick(ParCoolActionEvent.Tick.Pre event) {
        if (!(event.getAction() instanceof LeapSkill leapSkill)) return;

        var parryTime = leapSkill.parcoolskill$getParryTime();
        if (parryTime == 0) return;

        leapSkill.parcoolskill$setParryTime(parryTime - 1);
    }

    @SubscribeEvent
    public static void onPlayerAttack(PlayerAttackEvent.Pre event) {
        var player = event.getEntity();

        var leapSkill = (LeapSkill) Parkourability.get(player).get(CatLeap.class);
        if (!leapSkill.parcoolskill$isAttackReady()) return;
        leapSkill.parcoolskill$setAttackReady(false);

        if (!event.isFullStrength()) return;

        var wildStrikeStaminaConsumption = Config.WILD_STRIKE_STAMINA_CONSUMPTION.get();
        StaminaHandler.consume(player, wildStrikeStaminaConsumption);

        var wildStrikeDamageMultiplier = Config.WILD_STRIKE_DAMAGE_MULTIPLIER.get().floatValue();
        var wildStrikeParryDuration = Config.WILD_STRIKE_PARRY_DURATION.get();
        event.setCriticalHit(true);
        event.setDisableCrit(true);
        event.setDamageMultiplier(event.getDamageMultiplier() * wildStrikeDamageMultiplier);
        leapSkill.parcoolskill$setParryTime(wildStrikeParryDuration);

        var target = event.getTarget();
        SkillParticleHandler.emit(SkillParticlePayload.Type.IRONCLAD_HIT, target);
    }
}
