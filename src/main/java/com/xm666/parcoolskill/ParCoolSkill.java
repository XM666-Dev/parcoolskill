package com.xm666.parcoolskill;

import com.mojang.logging.LogUtils;
import com.xm666.parcoolskill.effect.Effects;
import com.xm666.parcoolskill.network.PayloadHandler;
import com.xm666.parcoolskill.particle.ParticleTypes;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(ParCoolSkill.MODID)
public class ParCoolSkill {
    public static final String MODID = "parcoolskill";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ParCoolSkill(FMLJavaModLoadingContext context) {
        this(context.getContainer(), context.getModEventBus());
    }

    public ParCoolSkill(ModContainer container, IEventBus eventBus) {
        Config.init(container);
        ClientConfig.init(container);
        Effects.init(eventBus);
        ParticleTypes.init(eventBus);
        PayloadHandler.init();
    }
}
