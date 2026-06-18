package com.xm666.parcoolskill.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlocksAttacks;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class LivingBlockEvent extends LivingEvent {
    private ItemStack itemBlockingWith;
    private BlocksAttacks blocksAttacks;

    public LivingBlockEvent(LivingEntity entity, ItemStack itemBlockingWith, BlocksAttacks blocksAttacks) {
        super(entity);
        this.itemBlockingWith = itemBlockingWith;
        this.blocksAttacks = blocksAttacks;
    }

    public ItemStack getItemBlockingWith() {
        return itemBlockingWith;
    }

    public void setItemBlockingWith(ItemStack itemBlockingWith) {
        this.itemBlockingWith = itemBlockingWith;
    }

    public BlocksAttacks getBlocksAttacks() {
        return this.blocksAttacks;
    }

    public void setBlocksAttacks(BlocksAttacks blocksAttacks) {
        this.blocksAttacks = blocksAttacks;
    }
}
