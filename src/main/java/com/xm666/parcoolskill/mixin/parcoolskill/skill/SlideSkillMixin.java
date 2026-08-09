package com.xm666.parcoolskill.mixin.parcoolskill.skill;

import com.alrex.parcool.common.action.impl.Slide;
import com.xm666.parcoolskill.handler.SkillHandler;
import com.xm666.parcoolskill.network.SkillPayload;
import com.xm666.parcoolskill.skill.SlideSkill;
import com.xm666.parcoolskill.skill.handler.SlideSkillHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class SlideSkillMixin {
    @Mixin(Slide.class)
    public static class SlideMixin implements SlideSkill {
        @Unique
        private Type parcoolskill$readyType = Type.NONE;
        @Unique
        private int parcoolskill$invulnerableTime;
        @Unique
        private boolean parcoolskill$invulnerable;

        @Override
        public Type parcoolskill$getReadyType() {
            return parcoolskill$readyType;
        }

        @Override
        public void parcoolskill$setReadyType(Type readyType) {
            parcoolskill$readyType = readyType;
        }

        @Override
        public int parcoolskill$getInvulnerableTime() {
            return parcoolskill$invulnerableTime;
        }

        @Override
        public void parcoolskill$setInvulnerableTime(int invulnerableTime) {
            parcoolskill$invulnerableTime = invulnerableTime;
        }

        @Override
        public boolean parcoolskill$isInvulnerable() {
            return parcoolskill$invulnerable;
        }

        @Override
        public void parcoolskill$setInvulnerable(boolean invulnerable) {
            parcoolskill$invulnerable = invulnerable;
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Mixin(Minecraft.class)
    private static class MinecraftMixin {
        @Inject(method = "handleKeybinds", at = @At("HEAD"))
        private void onHandleKeybinds(CallbackInfo ci) {
            var mc = Minecraft.getInstance();
            var player = mc.player;
            if (player == null || !(mc.hitResult instanceof EntityHitResult entityHitResult)) {
                SlideSkillHandler.tryPushBlock((BlockHitResult) mc.hitResult);
                return;
            }

            var readyType = SlideSkillHandler.getReadyType(player);
            if (readyType == SlideSkill.Type.NONE) return;

            var type = SkillPayload.Type.values()[readyType.ordinal()];
            var target = entityHitResult.getEntity();
            SkillHandler.use(type, player, target);
            player.resetAttackStrengthTicker();
        }
    }
}
