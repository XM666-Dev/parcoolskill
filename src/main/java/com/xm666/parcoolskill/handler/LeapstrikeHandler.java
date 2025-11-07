package com.xm666.parcoolskill.handler;

import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import com.alrex.parcool.common.action.impl.CatLeap;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.xm666.parcoolskill.ParCoolSkill;
import com.xm666.parcoolskill.action.LeapstrikeLeap;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber(modid = ParCoolSkill.MODID)
public class LeapstrikeHandler {
    @SubscribeEvent
    static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        var source = event.getSource();
        var sourceEntity = source.getEntity();
        if (sourceEntity instanceof Player player && source.is(DamageTypes.PLAYER_ATTACK)) {
            var leap = (LeapstrikeLeap) Parkourability.get(player).get(CatLeap.class);
            if (!leap.parcoolskill$isQueueAttack()) return;
            event.setAmount(event.getAmount() * 2.0F);
            player.magicCrit(event.getEntity());
            leap.parcoolskill$setQueueAttack(false);
            leap.parcoolskill$setQueueInvulnerable(true);
        } else if (event.getEntity() instanceof Player player && !event.getSource().is(DamageTypeTags.BYPASSES_ARMOR)) {
            var leap = (LeapstrikeLeap) Parkourability.get(player).get(CatLeap.class);
            if (!leap.parcoolskill$isQueueInvulnerable()) return;
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    static void onCatLeapStart(ParCoolActionEvent.StartEvent event) {
        if (!(event.getAction() instanceof LeapstrikeLeap leap)) return;
        leap.parcoolskill$setQueueAttack(true);
    }

    @SubscribeEvent
    static void onCatLeapStop(ParCoolActionEvent.StopEvent event) {
        if (!(event.getAction() instanceof LeapstrikeLeap leap)) return;
        leap.parcoolskill$setQueueAttack(false);
        leap.parcoolskill$setQueueInvulnerable(false);
    }
}
