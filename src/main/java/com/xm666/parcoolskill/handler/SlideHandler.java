package com.xm666.parcoolskill.handler;

import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import com.alrex.parcool.common.action.impl.Crawl;
import com.alrex.parcool.common.action.impl.Slide;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.xm666.parcoolskill.ParCoolSkill;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = ParCoolSkill.MODID)
public class SlideHandler {
    public static boolean flipPose;

    @SubscribeEvent
    static void onSlideStop(ParCoolActionEvent.StopEvent event) {
        if (!(event.getAction() instanceof Slide)) return;
        if (Parkourability.get(event.getPlayer()).get(Crawl.class).isDoing()) return;
        flipPose = true;
    }

    @SubscribeEvent
    static void onPlayerTick(PlayerTickEvent.Post event) {
        var player = event.getEntity();
        if (player.swimAmount == 0.0F) {
            flipPose = false;
        }
    }
}
