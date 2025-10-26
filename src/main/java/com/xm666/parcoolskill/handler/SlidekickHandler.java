package com.xm666.parcoolskill.handler;

import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import com.alrex.parcool.common.action.impl.Dodge;
import com.alrex.parcool.common.action.impl.Slide;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.xm666.parcoolskill.ParCoolSkill;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber(modid = ParCoolSkill.MODID)
public class SlidekickHandler {
    public static boolean queueAttack;
    public static boolean queueInvulnerable;

    @SubscribeEvent
    static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof Player && !event.getSource().is(DamageTypeTags.BYPASSES_ARMOR) && queueInvulnerable) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onSlideStart(ParCoolActionEvent.StartEvent event) {
        if (!(event.getAction() instanceof Slide)) return;
        if (!Parkourability.get(event.getPlayer()).get(Dodge.class).isDoing()) return;
        queueAttack = true;
        queueInvulnerable = true;
    }

    @SubscribeEvent
    public static void onSlideStop(ParCoolActionEvent.StopEvent event) {
        if (!(event.getAction() instanceof Slide)) return;
        queueAttack = false;
        queueInvulnerable = false;
    }
}
