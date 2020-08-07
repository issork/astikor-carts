package de.mennomax.astikorcarts.util;

import com.electronwill.nightconfig.core.Config;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import de.mennomax.astikorcarts.config.AstikorCartsConfig;
import de.mennomax.astikorcarts.entity.PlowCartEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class PlowBlockHandler implements BiConsumer<PlayerEntity, BlockPos> {
    private final List<PlowExecutor> executors = new ArrayList<>(2);
    private final ItemStack stack;
    private final int slot;
    private final PlowCartEntity plow;

    /**
     * SupressWarnings because of cast from Object to List(String). Safe because the
     * object is a string list as defined in {@link AstikorCartsConfig}
     */
    public PlowBlockHandler(final ItemStack stackIn, final int slotIn, final PlowCartEntity plowIn) {
        this.stack = stackIn;
        this.slot = slotIn;
        this.plow = plowIn;
        final ImmutableList.Builder<Pair<Predicate<? super BlockState>, BlockState>> replacements = new ImmutableList.Builder<>();
        if (!stackIn.isEmpty()) {
            for (final Config config : AstikorCartsConfig.COMMON.plowReplace.get()) {
                final ResourceLocation tool = ResourceLocation.tryCreate(config.get("tool"));
                if (tool == null) continue;
                final Tag<Item> tag = ItemTags.getCollection().get(tool);
                if (tag != null && tag.contains(this.stack.getItem()) || ForgeRegistries.ITEMS.containsKey(tool) && ForgeRegistries.ITEMS.getValue(tool) == this.stack.getItem()) {
                    final List<Config> blocks = config.get("blocks");
                    for (final Config block : blocks) {
                        final ResourceLocation result = ResourceLocation.tryCreate(block.get("result"));
                        if (result == null) continue;
                        final Block resultBlock = ForgeRegistries.BLOCKS.getValue(result);
                        if (resultBlock == null) continue;
                        final List<String> targets = block.get("targets");
                        targets.stream()
                            .map(ResourceLocation::tryCreate)
                            .filter(Objects::nonNull)
                            .<Predicate<BlockState>>flatMap(rl -> {
                                final Tag<Block> targetTag = BlockTags.getCollection().get(rl);
                                if (targetTag != null) {
                                    return Stream.of(s -> s.isIn(targetTag));
                                }
                                if (ForgeRegistries.BLOCKS.containsKey(rl)) {
                                    final Block target = ForgeRegistries.BLOCKS.getValue(rl);
                                    return Stream.of(s -> s.getBlock() == target);
                                }
                                return Stream.empty();
                            }).reduce(Predicate::or)
                            .ifPresent(predicate -> replacements.add(Pair.of(predicate, resultBlock.getDefaultState())));
                    }
                }
            }
        }
        this.executors.add(new ReplaceHandler(replacements.build()));
        // Config breakOrPlaceMap =
        // AstikorCartsConfig.COMMON.BREAKMAP.get().get(stack.getItem().getRegistryName().toString());
        // if(breakOrPlaceMap == null) {
        // if((breakOrPlaceMap = AstikorCartsConfig.COMMON.PLACEMAP.get()) != null) {
        // executors.add(new ReplaceHandler(AstikorCartsConfig.COMMON.PLACEMAP.get()));
        // }
        // }
    }

    @Override
    public void accept(final PlayerEntity player, final BlockPos blockPos) {
        for (final PlowExecutor exe : this.executors) {
            exe.tillBlock(player, blockPos);
        }
    }

    private class ReplaceHandler implements PlowExecutor {
        private final ImmutableList<Pair<Predicate<? super BlockState>, BlockState>> replaceMap;

        public ReplaceHandler(final ImmutableList<Pair<Predicate<? super BlockState>, BlockState>> replaceMapIn) {
            this.replaceMap = replaceMapIn;
        }

        @Override
        public void tillBlock(final PlayerEntity player, final BlockPos pos) {
            final World world = player.world;
            final BlockState toReplaceState = world.getBlockState(pos);
            this.replaceMap.stream()
                .filter(p -> p.getFirst().test(toReplaceState))
                .findFirst()
                .map(Pair::getSecond)
                .ifPresent(replaceWith -> {
                    final BreakEvent event = new BlockEvent.BreakEvent(world, pos, toReplaceState, player);
                    MinecraftForge.EVENT_BUS.post(event);
                    if (!event.isCanceled()) {
                        world.setBlockState(pos, replaceWith);
                        PlowBlockHandler.this.handleStackDamage(player, PlowBlockHandler.this.stack);
                    }
                });
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
                    this.plow.world.playSound(this.plow.getPosX(), this.plow.getPosY(), this.plow.getPosZ(), SoundEvents.ENTITY_ITEM_BREAK, this.plow.getSoundCategory(), 0.8F, 0.8F + this.plow.world.rand.nextFloat() * 0.4F, false);
                }
            } else {
                stack.shrink(1);
                if (stack.isEmpty()) {
                    this.plow.updateSlot(this.slot);
                }
            }
        }
    }
}
