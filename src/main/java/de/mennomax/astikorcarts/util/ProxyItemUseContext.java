package de.mennomax.astikorcarts.util;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;

public class ProxyItemUseContext extends UseOnContext {
    public ProxyItemUseContext(final Player player, final ItemStack itemstack, final BlockHitResult rayTraceResultIn) {
        super(player.level, player, InteractionHand.MAIN_HAND, itemstack, rayTraceResultIn);
    }
}
