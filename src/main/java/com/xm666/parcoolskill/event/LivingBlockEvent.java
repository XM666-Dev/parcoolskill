package com.xm666.parcoolskill.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlocksAttacks;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class LivingBlockEvent extends LivingEvent {
    private ItemStack item;
    private BlocksAttacks blocksAttacks;

    public LivingBlockEvent(LivingEntity entity, ItemStack item, BlocksAttacks blocksAttacks) {
        super(entity);
        this.item = item;
        this.blocksAttacks = blocksAttacks;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public BlocksAttacks blocksAttacks() {
        return this.blocksAttacks;
    }

    public void setBlocksAttacks(BlocksAttacks blocksAttacks) {
        this.blocksAttacks = blocksAttacks;
    }
}
