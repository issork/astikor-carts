package de.mennomax.astikorcarts.util;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.Config.Entry;
import de.mennomax.astikorcarts.config.AstikorCartsConfig;
import de.mennomax.astikorcarts.entity.PlowCartEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShovelItem;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlowBlockHandler {

    private final List<PlowExecutor> executors = new ArrayList<>(2);
    private final ItemStack stack;
    private final int slot;
    private final PlowCartEntity plow;

    /**
     * SupressWarnings because of cast from Object to List(String). Safe because the
     * object is a string list as defined in {@link AstikorCartsConfig}
     */
    @SuppressWarnings("unchecked")
    public PlowBlockHandler(final ItemStack stackIn, final int slotIn, final PlowCartEntity plowIn) {
        this.stack = stackIn;
        this.slot = slotIn;
        this.plow = plowIn;
        final String registryName = this.stack.getItem().getRegistryName().toString();
        Config itemReplaceMap = AstikorCartsConfig.COMMON.plowReplace.get().get(registryName);
        if (itemReplaceMap == null) {
            // TODO: Remove once #6236 has been merged
            if (this.stack.getItem() instanceof HoeItem) {
                itemReplaceMap = AstikorCartsConfig.COMMON.plowReplace.get().get("#forge:tools/hoes");
            } else if (this.stack.getItem() instanceof ShovelItem) {
                itemReplaceMap = AstikorCartsConfig.COMMON.plowReplace.get().get("#forge:tools/shovels");
            } else {
                for (final ResourceLocation rl : this.stack.getItem().getTags()) {
                    if ((itemReplaceMap = AstikorCartsConfig.COMMON.plowReplace.get().get(rl.toString())) != null) {
                        break;
                    }
                }
            }
        }
        if (itemReplaceMap != null) {
            final HashMap<ArrayList<Block>, Block> replaceMap = new HashMap<>();
            for (final Entry entry : itemReplaceMap.entrySet()) {
                final ArrayList<Block> blockList = new ArrayList<>();
                for (final String blockId : ((List<String>) entry.getValue())) {
                    final Block toAdd = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockId));
                    if (toAdd == null) {
                        blockList.addAll(BlockTags.getCollection().get(new ResourceLocation(blockId)).getAllElements());
                    } else {
                        blockList.add(toAdd);
                    }
                }
                replaceMap.put(blockList, ForgeRegistries.BLOCKS.getValue(new ResourceLocation(entry.getKey())));
            }
            this.executors.add(new ReplaceHandler(replaceMap));
        }
        // Config breakOrPlaceMap =
        // AstikorCartsConfig.COMMON.BREAKMAP.get().get(stack.getItem().getRegistryName().toString());
        // if(breakOrPlaceMap == null) {
        // if((breakOrPlaceMap = AstikorCartsConfig.COMMON.PLACEMAP.get()) != null) {
        // executors.add(new ReplaceHandler(AstikorCartsConfig.COMMON.PLACEMAP.get()));
        // }
        // }
    }

    public void tillBlock(final PlayerEntity player, final BlockPos blockPos) {
        for (final PlowExecutor exe : this.executors) {
            exe.tillBlock(player, blockPos);
        }
    }

    private class ReplaceHandler implements PlowExecutor {

        private final HashMap<ArrayList<Block>, Block> replaceMap;

        public ReplaceHandler(final HashMap<ArrayList<Block>, Block> replaceMapIn) {
            this.replaceMap = replaceMapIn;
        }

        @Override
        public void tillBlock(final PlayerEntity player, final BlockPos pos) {
            final BlockState toReplaceState = player.world.getBlockState(pos);
            final Block replaceWith = PlowBlockHandler.this.getFirstMatch(this.replaceMap, toReplaceState);
            if (replaceWith != null) {
                final BreakEvent event = new BlockEvent.BreakEvent(player.world, pos, toReplaceState, player);
                MinecraftForge.EVENT_BUS.post(event);
                if (!event.isCanceled()) {
                    player.world.setBlockState(pos, replaceWith.getDefaultState());
                    PlowBlockHandler.this.handleStackDamage(player, PlowBlockHandler.this.stack);
                }
            }
        }

    }

    // private class BreakHandler implements PlowExecutor {
    //
    // private List<Block> breakList;
    //
    // public BreakHandler(List<Block> breakListIn) {
    // this.breakList = breakListIn;
    // }
    //
    // @Override
    // public void plowBlock(PlayerEntity player, BlockPos pos) {
    //
    // }
    //
    // }
    //
    // private class PlaceHandler implements PlowExecutor {
    //
    // @Override
    // public void tillBlock(PlayerEntity player, BlockPos pos) {
    //
    // }
    //
    // }

    private void handleStackDamage(final PlayerEntity player, final ItemStack stack) {
        if (!player.isCreative()) {
            if (stack.isDamageable()) {
                final ItemStack copy = stack.copy();
                stack.damageItem(1, player, e -> {});
                if (stack.isEmpty()) {
                    ForgeEventFactory.onPlayerDestroyItem(player, copy, null);
                    this.plow.updateSlot(this.slot);
                    this.plow.world.playSound(this.plow.posX, this.plow.posY, this.plow.posZ, SoundEvents.ENTITY_ITEM_BREAK, this.plow.getSoundCategory(), 0.8F, 0.8F + this.plow.world.rand.nextFloat() * 0.4F, false);
                }
            } else {
                stack.shrink(1);
                if (stack.isEmpty()) {
                    this.plow.updateSlot(this.slot);
                }
            }
        }
    }

    private Block getFirstMatch(final HashMap<ArrayList<Block>, Block> replaceMap, final BlockState toReplaceState) {
        for (final ArrayList<Block> matchList : replaceMap.keySet()) {
            for (final Block match : matchList) {
                if (match == toReplaceState.getBlock()) {
                    return replaceMap.get(matchList);
                }
            }
        }
        return null;
    }

}
