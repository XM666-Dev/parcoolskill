package com.xm666.parcoolskill.skill.handler;

import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import com.alrex.parcool.common.action.impl.CatLeap;
import com.alrex.parcool.common.action.impl.ChargeJump;
import com.alrex.parcool.common.action.impl.Flipping;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.xm666.parcoolskill.Config;
import com.xm666.parcoolskill.ParCoolSkill;
import com.xm666.parcoolskill.effect.Effects;
import com.xm666.parcoolskill.event.PlayerAttackEvent;
import com.xm666.parcoolskill.handler.SkillHandler;
import com.xm666.parcoolskill.handler.StaminaHandler;
import com.xm666.parcoolskill.network.SkillParticlePayload;
import com.xm666.parcoolskill.particle.SkillParticleHandler;
import com.xm666.parcoolskill.skill.JumpSkill;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;

@EventBusSubscriber(modid = ParCoolSkill.MODID)
public class BashHandler {
    @SubscribeEvent
    public static void onJumpStart(ParCoolActionEvent.Start.Pre event) {
        if (!Config.BASH_ENABLED.get() || !(event.getAction() instanceof JumpSkill jumpSkill)) return;

        jumpSkill.parcoolskill$setAttackReady(true);
    }

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        var parkourability = Parkourability.get(player);
        var jumpSkill = (JumpSkill) parkourability.get(ChargeJump.class);
        jumpSkill.parcoolskill$setAttackReady(false);
    }

    @SubscribeEvent
    public static void onPlayerAttack(PlayerAttackEvent.Post event) {
        var player = event.getEntity();
        var parkourability = Parkourability.get(player);
        if (parkourability.get(CatLeap.class).isDoing()
                || parkourability.get(Flipping.class).isDoing()
                || !isAttackReady(player)
                || !event.isVanillaCritical()) return;

        var bashStaminaConsumption = Config.BASH_STAMINA_CONSUMPTION.get();
        StaminaHandler.consume(player, bashStaminaConsumption);

        var target = event.getTarget();
        if (target instanceof LivingEntity living) {
            var bashVulnerableDuration = Config.BASH_VULNERABLE_DURATION.get();
            SkillHandler.addEffect(living, player, Effects.VULNERABLE, bashVulnerableDuration);
        }

        event.setDisableCrit(true);
        SkillParticleHandler.emit(SkillParticlePayload.Type.IRONCLAD_HIT, target);
    }

    private static boolean isAttackReady(Player player) {
        var parkourability = Parkourability.get(player);
        var jumpSkill = (JumpSkill) parkourability.get(ChargeJump.class);
        if (!jumpSkill.parcoolskill$isAttackReady()) return false;

        jumpSkill.parcoolskill$setAttackReady(false);
        return true;
    }
}
