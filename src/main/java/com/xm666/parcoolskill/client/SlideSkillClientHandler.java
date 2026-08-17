package com.xm666.parcoolskill.client;

import com.alrex.parcool.common.action.impl.Slide;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.xm666.parcoolskill.skill.SlideSkill;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class SlideSkillClientHandler {
    @OnlyIn(Dist.CLIENT)
    public static void tryPushBlock(BlockHitResult hitResult) {
        if (hitResult.getType() == HitResult.Type.MISS) return;

        var mc = Minecraft.getInstance();
        var player = mc.player;
        var parkourability = Parkourability.get(player);
        var slideSkill = (SlideSkill) parkourability.get(Slide.class);
        var readyType = slideSkill.parcoolskill$getReadyType();
        if (readyType == SlideSkill.Type.NONE) return;

        var level = mc.level;
        var blockPos = hitResult.getBlockPos();
        var blockState = level.getBlockState(blockPos);
        var block = blockState.getBlock();
        if (!(block instanceof DoorBlock doorBlock) || !doorBlock.type().canOpenByHand()) return;

        var direction = blockState.getValue(DoorBlock.FACING);
        var open = blockState.getValue(DoorBlock.OPEN);
        var openDirection = direction.getOpposite();
        if (open) {
            var hinge = blockState.getValue(DoorBlock.HINGE);
            openDirection = hinge == DoorHingeSide.LEFT ? openDirection.getClockWise() : openDirection.getCounterClockWise();
        }
        if (openDirection != hitResult.getDirection()) return;

        var sound = readyType == SlideSkill.Type.DROPKICK ? SoundEvents.PLAYER_ATTACK_KNOCKBACK : SoundEvents.PLAYER_ATTACK_STRONG;
        mc.gameMode.useItemOn(player, InteractionHand.MAIN_HAND, hitResult);
        level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, blockPos, Block.getId(blockState));
        level.playLocalSound(player, sound, player.getSoundSource(), 1.0F, 1.0F);
    }
}
