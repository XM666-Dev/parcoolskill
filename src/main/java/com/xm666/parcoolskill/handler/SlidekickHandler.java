package com.xm666.parcoolskill.handler;

import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import com.alrex.parcool.common.action.impl.Dodge;
import com.alrex.parcool.common.action.impl.Slide;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.xm666.parcoolskill.ParCoolSkill;
import com.xm666.parcoolskill.action.SlidekickSlide;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber(modid = ParCoolSkill.MODID)
public class SlidekickHandler {
    @SubscribeEvent
    static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof Player player && !event.getSource().is(DamageTypeTags.BYPASSES_ARMOR)) {
            var slide = (SlidekickSlide) Parkourability.get(player).get(Slide.class);
            if (!slide.parcoolskill$isQueueInvulnerable() || !((Slide) slide).isDoing() && ((Slide) slide).getNotDoingTick() > 10)
                return;
            event.setCanceled(true);
        }
    }

    static void onSlideStart(ParCoolActionEvent.StartEvent event) {
        if (!(event.getAction() instanceof SlidekickSlide slide)) return;
        if (!Parkourability.get(event.getPlayer()).get(Dodge.class).isDoing()) return;
        slide.parcoolskill$setQueueSlidekick(true);
        slide.parcoolskill$setQueueInvulnerable(true);
    }

    @SubscribeEvent
    static void onSlideStop(ParCoolActionEvent.StopEvent event) {
        if (!(event.getAction() instanceof SlidekickSlide slide)) return;
        slide.parcoolskill$setQueueSlidekick(false);
    }
}
