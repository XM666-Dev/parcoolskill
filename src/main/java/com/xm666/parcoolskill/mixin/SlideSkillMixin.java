package com.xm666.parcoolskill.mixin;

import com.alrex.parcool.common.action.impl.Slide;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.xm666.parcoolskill.handler.SkillAttackHandler;
import com.xm666.parcoolskill.network.SkillAttackPayload;
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
        private ReadyAttackType parcoolskill$readyAttackType = ReadyAttackType.NONE;
        @Unique
        private int parcoolskill$invulnerableTime;

        @Override
        public ReadyAttackType parcoolskill$getReadyAttackType() {
            return parcoolskill$readyAttackType;
        }

        @Override
        public void parcoolskill$setReadyAttackType(ReadyAttackType readyAttackType) {
            this.parcoolskill$readyAttackType = readyAttackType;
        }

        @Override
        public int parcoolskill$getInvulnerableTime() {
            return parcoolskill$invulnerableTime;
        }

        @Override
        public void parcoolskill$setInvulnerableTime(int invulnerableTime) {
            this.parcoolskill$invulnerableTime = invulnerableTime;
        }
    }

    @Mixin(Minecraft.class)
    private static class MinecraftMixin {
        @Inject(method = "handleKeybinds", at = @At("HEAD"))
        private void onHandleKeybinds(CallbackInfo ci) {
            var mc = Minecraft.getInstance();
            var player = mc.player;
            if (!(mc.hitResult instanceof EntityHitResult entityHitResult) || player == null) return;

            var skillSlide = (SlideSkill) Parkourability.get(player).get(Slide.class);
            var readyAttackType = skillSlide.parcoolskill$getReadyAttackType();
            if (readyAttackType == SlideSkill.ReadyAttackType.NONE) return;
            skillSlide.parcoolskill$setReadyAttackType(SlideSkill.ReadyAttackType.NONE);

            var target = entityHitResult.getEntity();
            var skillAttackType = SkillAttackPayload.SkillAttackType.values()[readyAttackType.ordinal()];
            SkillAttackHandler.attack(target, player, skillAttackType);
            player.resetAttackStrengthTicker();
        }
    }
}
