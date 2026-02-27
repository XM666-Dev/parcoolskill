package com.xm666.parcoolskill.mixin;

import com.alrex.parcool.common.action.impl.Slide;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.xm666.parcoolskill.handler.SkillHandler;
import com.xm666.parcoolskill.network.SkillPayload;
import com.xm666.parcoolskill.skill.SlideSkill;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.EntityHitResult;
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
        private boolean parcoolskill$disableSliding;

        @Override
        public Type parcoolskill$getReadyType() {
            return parcoolskill$readyType;
        }

        @Override
        public void parcoolskill$setReadyType(Type readyType) {
            this.parcoolskill$readyType = readyType;
        }

        @Override
        public int parcoolskill$getInvulnerableTime() {
            return parcoolskill$invulnerableTime;
        }

        @Override
        public void parcoolskill$setInvulnerableTime(int invulnerableTime) {
            this.parcoolskill$invulnerableTime = invulnerableTime;
        }

        @Override
        public boolean parcoolskill$isDisableSliding() {
            return parcoolskill$disableSliding;
        }

        @Override
        public void parcoolskill$setDisableSliding(boolean disableSliding) {
            parcoolskill$disableSliding = disableSliding;
        }
    }

    @Mixin(Minecraft.class)
    private static class MinecraftMixin {
        @Inject(method = "handleKeybinds", at = @At("HEAD"))
        private void onHandleKeybinds(CallbackInfo ci) {
            var mc = Minecraft.getInstance();
            if (!(mc.hitResult instanceof EntityHitResult entityHitResult)) return;

            var player = mc.player;
            if (player == null) return;

            var slideSkill = (SlideSkill) Parkourability.get(player).get(Slide.class);
            var readyType = slideSkill.parcoolskill$getReadyType();
            if (readyType == SlideSkill.Type.NONE) return;
            slideSkill.parcoolskill$setReadyType(SlideSkill.Type.NONE);

            var type = SkillPayload.Type.values()[readyType.ordinal()];
            var target = entityHitResult.getEntity();
            SkillHandler.use(type, player, target);
            player.resetAttackStrengthTicker();
        }
    }
}
