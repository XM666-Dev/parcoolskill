package com.xm666.parcoolskill.handler;

import com.xm666.parcoolskill.ParCoolSkill;
import com.xm666.parcoolskill.network.BulletTimePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@EventBusSubscriber(modid = ParCoolSkill.MODID)
public class BulletTimeHandler {
    public static Ticker clientTicker = new Ticker();
    public static Ticker serverTicker = new Ticker();
    public static boolean modifyRunsNormally = true;
    public static boolean modifyGameTimeDeltaPartialTick = true;
    public static boolean canRunsNormally = true;

    @SubscribeEvent
    static void onClientTick(ClientTickEvent.Pre event) {
        if (!Minecraft.getInstance().isPaused()) {
            clientTicker.tick();
        }
    }

    @SubscribeEvent
    static void onServerTick(ServerTickEvent.Pre event) {
        serverTicker.tick();
    }

    //@SubscribeEvent
    //static void onRender(RenderFrameEvent.Pre event) {
    //    if (!Minecraft.getInstance().isPaused()) {
    //        ParCoolSkill.LOGGER.info("partialTick = {}", event.getPartialTick().getGameTimeDeltaPartialTick(false));
    //    }
    //}

    public static void addScale(float timeScale, int timeScaleTicks) {
        addScale(serverTicker, timeScale, timeScaleTicks);
        PacketDistributor.sendToAllPlayers(new BulletTimePayload(timeScale, timeScaleTicks));
    }

    public static void handlePayload(final BulletTimePayload payload, final IPayloadContext context) {
        addScale(clientTicker, payload.timeScale(), payload.timeScaleTicks());
    }

    public static void addScale(Ticker ticker, float timeScale, int timeScaleTicks) {
        ticker.timeScale = ticker.timeScaleTicks > 0 ? Math.min(ticker.timeScale, timeScale) : timeScale;
        ticker.timeScaleTicks = Math.min(ticker.timeScaleTicks + timeScaleTicks, 600);
    }

    public static class Ticker {
        public float timeScale;
        public int timeScaleTicks;
        public float partialTick;
        public boolean runTick;

        public float getDefaultTimeScale() {
            return 1.0F;
        }

        public float getTimeScale() {
            return timeScaleTicks > 0 ? timeScale : getDefaultTimeScale();
        }

        public void tick() {
            if (timeScaleTicks > 0) --timeScaleTicks;
            partialTick += getTimeScale();
            runTick = partialTick >= 1.0;
            partialTick -= Mth.floor(partialTick);
        }
    }
}
