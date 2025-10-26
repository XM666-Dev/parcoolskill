package com.xm666.parcoolskill;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(ParCoolSkill.MODID)
public class ParCoolSkill {
    public static final String MODID = "parcoolskill";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ParCoolSkill(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
