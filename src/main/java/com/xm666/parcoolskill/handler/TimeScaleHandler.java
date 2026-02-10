package com.xm666.parcoolskill.handler;

import com.xm666.parcoolskill.ParCoolSkill;
import com.xm666.parcoolskill.network.TimeScalePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@EventBusSubscriber(modid = ParCoolSkill.MODID)
public class TimeScaleHandler {
    public static final ScalableTimer clientTimer = new ScalableTimer();
    public static final ScalableTimer serverTimer = new ScalableTimer();
    public static boolean modifyRunsNormally = true;
    public static boolean enableRunsNormally = true;
    public static boolean modifyGameTimeDeltaPartialTick = true;
    public static float deltaTickRunning;

    @SubscribeEvent
    static void onClientTick(ClientTickEvent.Pre event) {
        var mc = Minecraft.getInstance();
        if (mc.level == null || mc.isPaused()) return;

        clientTimer.tick();
        deltaTickRunning = clientTimer.runsTicking() ? 0.0F : deltaTickRunning + clientTimer.getScale();
    }

    @SubscribeEvent
    static void onServerTick(ServerTickEvent.Pre event) {
        serverTimer.tick();
    }

    public static void handlePayload(final TimeScalePayload payload, final IPayloadContext context) {
        clientTimer.applyScale(payload.scale(), payload.scaleTicks());
    }

    public static void applyScale(float scale, int scaleTicks) {
        serverTimer.applyScale(scale, scaleTicks);
        PacketDistributor.sendToAllPlayers(new TimeScalePayload(scale, scaleTicks));
    }

    public static class ScalableTimer {
        private float scale;
        private int scaleTicks;
        private float tickScale;
        private boolean runTick;
        private float deltaTickResidual;

        private static float clampedInverseLerp(float delta, float start, float end) {
            var inverseDelta = Mth.inverseLerp(delta, start, end);
            return Mth.clamp(inverseDelta, 0.0F, 1.0F);
        }

        private static float smoothstep(float input) {
            return input * input * input * (input * (input * 6.0F - 15.0F) + 10.0F);
        }

        private float getDefaultTimeScale() {
            return 1.0F;
        }

        private float calculateTimeScale() {
            var delta = clampedInverseLerp(scaleTicks, 20.0F, 0.0F);
            delta = smoothstep(delta);
            return Mth.lerp(delta, scale, getDefaultTimeScale());
        }

        public void tick() {
            tickScale = calculateTimeScale();
            scaleTicks = scaleTicks > 0 ? --scaleTicks : 0;
            var nextDeltaTickResidual = deltaTickResidual + tickScale;
            runTick = nextDeltaTickResidual >= 1.0F;
            deltaTickResidual = Mth.frac(nextDeltaTickResidual);
        }

        public void applyScale(float scale, int scaleTicks) {
            this.scale = this.scaleTicks > 0 ? this.scale * scale : scale;
            this.scaleTicks = scaleTicks;
        }

        public boolean runsTicking() {
            return runTick;
        }

        public float getScale() {
            return tickScale;
        }
    }
}
