package com.xm666.parcoolskill;

import com.mojang.logging.LogUtils;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(ParCoolSkill.MODID)
public class ParCoolSkill {
    public static final String MODID = "parcoolskill";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ParCoolSkill(ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
