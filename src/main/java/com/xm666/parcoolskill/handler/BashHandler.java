package com.xm666.parcoolskill.handler;

import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import com.alrex.parcool.common.action.impl.ChargeJump;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.xm666.parcoolskill.Config;
import com.xm666.parcoolskill.ParCoolSkill;
import com.xm666.parcoolskill.effect.Effects;
import com.xm666.parcoolskill.event.PlayerAttackEvent;
import com.xm666.parcoolskill.network.SkillParticlePayload;
import com.xm666.parcoolskill.skill.JumpSkill;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = ParCoolSkill.MODID)
public class BashHandler {
    @SubscribeEvent
    public static void onJumpStart(ParCoolActionEvent.Start.Pre event) {
        if (!(event.getAction() instanceof JumpSkill jumpSkill)) return;

        jumpSkill.parcoolskill$setAttackReady(true);
    }

    @SubscribeEvent
    public static void onJumpFinish(ParCoolActionEvent.Finish.Pre event) {
        if (!(event.getAction() instanceof JumpSkill jumpSkill)) return;

        jumpSkill.parcoolskill$setAttackReady(false);
    }

    @SubscribeEvent
    public static void onPlayerAttack(PlayerAttackEvent.Post event) {
        var player = event.getEntity();

        var jumpSkill = (JumpSkill) Parkourability.get(player).get(ChargeJump.class);
        if (!jumpSkill.parcoolskill$isAttackReady()) return;
        jumpSkill.parcoolskill$setAttackReady(false);

        if (!event.getCritEvent().isVanillaCritical()) return;

        var bashVulnerableDuration = Config.BASH_VULNERABLE_DURATION.get();
        var target = event.getTarget();
        SkillHandler.addEffect(target, player, Effects.VULNERABLE, bashVulnerableDuration);

        SkillParticleHandler.emit(SkillParticlePayload.Type.IRONCLAD_HIT, target);
    }
}
