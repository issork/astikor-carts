package de.mennomax.astikorcarts.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;

public class ProxyItemUseContext extends ItemUseContext {
    public ProxyItemUseContext(final PlayerEntity player, final ItemStack itemstack, final BlockRayTraceResult rayTraceResultIn) {
        super(player.world, player, Hand.MAIN_HAND, itemstack, rayTraceResultIn);
    }
}
