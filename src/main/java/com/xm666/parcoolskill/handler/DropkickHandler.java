package com.xm666.parcoolskill.handler;

import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import com.alrex.parcool.common.action.impl.CatLeap;
import com.alrex.parcool.common.action.impl.Slide;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.xm666.parcoolskill.ParCoolSkill;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber(modid = ParCoolSkill.MODID)
public class DropkickHandler {
    public static boolean queueAttack;
    static boolean queueSlideInvulnerable;
    static boolean queueCatLeapInvulnerable;

    @SubscribeEvent
    static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof Player && !event.getSource().is(DamageTypeTags.BYPASSES_ARMOR) && (queueSlideInvulnerable || queueCatLeapInvulnerable)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    static void onSlideStart(ParCoolActionEvent.StartEvent event) {
        if (!(event.getAction() instanceof Slide)) return;
        if (!Parkourability.get(event.getPlayer()).get(CatLeap.class).isDoing()) return;
        queueAttack = true;
        queueSlideInvulnerable = true;
        queueCatLeapInvulnerable = true;
        var player = event.getPlayer();
        player.setDeltaMovement(player.getDeltaMovement().add(0.0, 0.2, 0.0));
    }

    @SubscribeEvent
    static void onSlideStop(ParCoolActionEvent.StopEvent event) {
        if (!(event.getAction() instanceof Slide)) return;
        queueAttack = false;
        queueSlideInvulnerable = false;
    }

    @SubscribeEvent
    static void onCatLeapStop(ParCoolActionEvent.StopEvent event) {
        if (!(event.getAction() instanceof CatLeap)) return;
        queueCatLeapInvulnerable = false;
    }
}
