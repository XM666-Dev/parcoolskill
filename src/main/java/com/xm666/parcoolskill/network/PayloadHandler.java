package com.xm666.parcoolskill.network;

import com.xm666.parcoolskill.ParCoolSkill;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = ParCoolSkill.MODID)
public class PayloadHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ParCoolSkill.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void init() {
        var index = 0;
        INSTANCE.registerMessage(index++, SkillPayload.class, SkillPayload::write, SkillPayload::read, SkillPayload::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(index++, SkillParticlePayload.class, SkillParticlePayload::write, SkillParticlePayload::read, SkillParticlePayload::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        INSTANCE.registerMessage(index++, StaminaPayload.class, StaminaPayload::write, StaminaPayload::read, StaminaPayload::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }
}
