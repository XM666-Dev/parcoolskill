package com.xm666.parcoolskill.handler;

import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import com.alrex.parcool.common.action.impl.CatLeap;
import com.alrex.parcool.common.action.impl.Slide;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.xm666.parcoolskill.ParCoolSkill;
import com.xm666.parcoolskill.action.DropkickSlide;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber(modid = ParCoolSkill.MODID)
public class DropkickHandler {
    @SubscribeEvent
    static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof Player player && !event.getSource().is(DamageTypeTags.BYPASSES_ARMOR)) {
            var slide = (DropkickSlide) Parkourability.get(player).get(Slide.class);
            if (!slide.parcoolskill$isQueueSlideInvulnerable() && !slide.parcoolskill$isQueueLeapInvulnerable()) return;
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    static void onSlideStart(ParCoolActionEvent.StartEvent event) {
        if (!(event.getAction() instanceof DropkickSlide slide)) return;
        if (!Parkourability.get(event.getPlayer()).get(CatLeap.class).isDoing()) return;
        slide.parcoolskill$setQueueAttack(true);
        slide.parcoolskill$setQueueSlideInvulnerable(true);
        slide.parcoolskill$setQueueLeapInvulnerable(true);
        var player = event.getPlayer();
        player.setDeltaMovement(player.getDeltaMovement().add(0.0, 0.2, 0.0));
    }

    @SubscribeEvent
    static void onSlideStop(ParCoolActionEvent.StopEvent event) {
        if (!(event.getAction() instanceof DropkickSlide slide)) return;
        slide.parcoolskill$setQueueAttack(false);
        slide.parcoolskill$setQueueSlideInvulnerable(false);
    }

    @SubscribeEvent
    static void onCatLeapStop(ParCoolActionEvent.StopEvent event) {
        if (!(event.getAction() instanceof CatLeap)) return;
        var slide = (DropkickSlide) Parkourability.get(event.getPlayer()).get(Slide.class);
        slide.parcoolskill$setQueueLeapInvulnerable(false);
    }
}
