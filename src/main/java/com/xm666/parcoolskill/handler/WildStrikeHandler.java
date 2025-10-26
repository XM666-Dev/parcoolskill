package com.xm666.parcoolskill.handler;

import com.xm666.parcoolskill.ParCoolSkill;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber(modid = ParCoolSkill.MODID)
public class WildStrikeHandler {
    public static boolean queueAttack;
    public static boolean queueInvulnerable;

    @SubscribeEvent
    static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        var source = event.getSource();
        var sourceEntity = source.getEntity();
        if (sourceEntity instanceof Player player && source.is(DamageTypes.PLAYER_ATTACK) && queueAttack) {
            event.setAmount(event.getAmount() * 2.0F);
            player.magicCrit(event.getEntity());
            queueAttack = false;
            queueInvulnerable = true;
        } else if (event.getEntity() instanceof Player && source.is(DamageTypes.MOB_ATTACK) && queueInvulnerable) {
            event.setCanceled(true);
        }
    }
}
