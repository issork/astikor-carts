package de.mennomax.astikorcarts.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

public class PlowBlockHandler {

    private final List<PlowExecutor> executors = new ArrayList<>(2);
    private final ItemStack STACK;
    private final int SLOT;
    private final PlowCartEntity PLOW;

    /**
     * SupressWarnings because of cast from Object to List(String). Safe because the
     * object is a string list as defined in {@link AstikorCartsConfig}
     */
    @SuppressWarnings("unchecked")
    public PlowBlockHandler(ItemStack stackIn, int slotIn, PlowCartEntity plowIn) {
        STACK = stackIn;
        SLOT = slotIn;
        PLOW = plowIn;
        String registryName = STACK.getItem().getRegistryName().toString();
        Config itemReplaceMap = AstikorCartsConfig.COMMON.REPLACEMAP.get().get(registryName);
        if (itemReplaceMap == null) {
            // TODO: Remove once #6236 has been merged
            if (STACK.getItem() instanceof HoeItem) {
                itemReplaceMap = AstikorCartsConfig.COMMON.REPLACEMAP.get().get("#forge:tools/hoes");
            } else if (STACK.getItem() instanceof ShovelItem) {
                itemReplaceMap = AstikorCartsConfig.COMMON.REPLACEMAP.get().get("#forge:tools/shovels");
            } else {
                for (ResourceLocation rl : STACK.getItem().getTags()) {
                    if ((itemReplaceMap = AstikorCartsConfig.COMMON.REPLACEMAP.get().get(rl.toString())) != null) {
                        break;
                    }
                }
            }
        }
        if (itemReplaceMap != null) {
            HashMap<ArrayList<Block>, Block> replaceMap = new HashMap<>();
            for (Entry entry : itemReplaceMap.entrySet()) {
                ArrayList<Block> blockList = new ArrayList<>();
                for (String blockId : ((List<String>) entry.getValue())) {
                    Block toAdd = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockId));
                    if (toAdd == null) {
                        for (Block block : BlockTags.getCollection().get(new ResourceLocation(blockId)).getAllElements()) {
                            blockList.add(block);
                        }
                    } else {
                        blockList.add(toAdd);
                    }
                }
                replaceMap.put(blockList, ForgeRegistries.BLOCKS.getValue(new ResourceLocation(entry.getKey())));
            }
            executors.add(new ReplaceHandler(replaceMap));
        }
        // Config breakOrPlaceMap =
        // AstikorCartsConfig.COMMON.BREAKMAP.get().get(stack.getItem().getRegistryName().toString());
        // if(breakOrPlaceMap == null) {
        // if((breakOrPlaceMap = AstikorCartsConfig.COMMON.PLACEMAP.get()) != null) {
        // executors.add(new ReplaceHandler(AstikorCartsConfig.COMMON.PLACEMAP.get()));
        // }
        // }
    }

    public void tillBlock(PlayerEntity player, BlockPos blockPos) {
        for (PlowExecutor exe : executors) {
            exe.tillBlock(player, blockPos);
        }
    }

    private class ReplaceHandler implements PlowExecutor {

        private final HashMap<ArrayList<Block>, Block> replaceMap;

        public ReplaceHandler(HashMap<ArrayList<Block>, Block> replaceMapIn) {
            this.replaceMap = replaceMapIn;
        }

        @Override
        public void tillBlock(PlayerEntity player, BlockPos pos) {
            BlockState toReplaceState = player.world.getBlockState(pos);
            Block replaceWith = getFirstMatch(replaceMap, toReplaceState);
            if (replaceWith != null) {
                BreakEvent event = new BlockEvent.BreakEvent(player.world, pos, toReplaceState, player);
                MinecraftForge.EVENT_BUS.post(event);
                if (!event.isCanceled()) {
                    player.world.setBlockState(pos, replaceWith.getDefaultState());
                    handleStackDamage(player, STACK);
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

    private void handleStackDamage(PlayerEntity player, ItemStack stack) {
        if (!player.isCreative()) {
            if (stack.isDamageable()) {
                ItemStack copy = stack.copy();
                stack.damageItem(1, player, e -> {});
                if (stack.isEmpty()) {
                    ForgeEventFactory.onPlayerDestroyItem(player, copy, null);
                    PLOW.updateSlot(SLOT);
                    PLOW.world.playSound(PLOW.posX, PLOW.posY, PLOW.posZ, SoundEvents.ENTITY_ITEM_BREAK, PLOW.getSoundCategory(), 0.8F, 0.8F + PLOW.world.rand.nextFloat() * 0.4F, false);
                }
            } else {
                stack.shrink(1);
                if (stack.isEmpty()) {
                    PLOW.updateSlot(SLOT);
                }
            }
        }
    }

    private Block getFirstMatch(HashMap<ArrayList<Block>, Block> replaceMap, BlockState toReplaceState) {
        for (ArrayList<Block> matchList : replaceMap.keySet()) {
            for (Block match : matchList) {
                if (match == toReplaceState.getBlock()) {
                    return replaceMap.get(matchList);
                }
            }
        }
        return null;
    }

}
