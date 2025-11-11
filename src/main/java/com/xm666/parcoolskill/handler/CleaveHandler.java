package com.xm666.parcoolskill.handler;

import com.xm666.parcoolskill.ParCoolSkill;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber(modid = ParCoolSkill.MODID)
public class CleaveHandler {
    public static boolean queueAttack;

    @SubscribeEvent
    static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        if (queueAttack) {
            var source = (Player) event.getSource().getEntity();
            if (source == null) return;
            event.setAmount(getCleaveDamage(source, event.getAmount()));
            queueAttack = false;
        }
    }

    public static float getCleaveDamage(LivingEntity source, float amount) {
        return 1.0F + ((float) source.getAttributeValue(Attributes.SWEEPING_DAMAGE_RATIO) + 1.25F) * amount;
    }

    public static double getCleaveScale() {
        return 2.0;
    }
}
