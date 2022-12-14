package de.mennomax.astikorcarts.entity;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonParseException;
import de.mennomax.astikorcarts.AstikorCarts;
import de.mennomax.astikorcarts.client.sound.CartingJukeboxSound;
import de.mennomax.astikorcarts.config.AstikorCartsConfig;
import de.mennomax.astikorcarts.inventory.container.SupplyCartContainer;
import de.mennomax.astikorcarts.util.CartItemStackHandler;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

public final class SupplyCartEntity extends AbstractDrawnInventoryEntity {
    private static final ImmutableList<EntityDataAccessor<ItemStack>> CARGO = ImmutableList.of(
        SynchedEntityData.defineId(SupplyCartEntity.class, EntityDataSerializers.ITEM_STACK),
        SynchedEntityData.defineId(SupplyCartEntity.class, EntityDataSerializers.ITEM_STACK),
        SynchedEntityData.defineId(SupplyCartEntity.class, EntityDataSerializers.ITEM_STACK),
        SynchedEntityData.defineId(SupplyCartEntity.class, EntityDataSerializers.ITEM_STACK));

    public SupplyCartEntity(final EntityType<? extends Entity> type, final Level world) {
        super(type, world);
    }

    @Override
    protected AstikorCartsConfig.CartConfig getConfig() {
        return AstikorCartsConfig.get().supplyCart;
    }

    @Override
    protected ItemStackHandler initInventory() {
        return new CartItemStackHandler<SupplyCartEntity>(54, this) {
            @Override
            protected void onLoad() {
                super.onLoad();
                this.onContentsChanged(0);
            }

            @Override
            protected void onContentsChanged(final int slot) {
                final Object2IntMap<Item> totals = new Object2IntLinkedOpenHashMap<>();
                final Object2ObjectMap<Item, ItemStack> stacks = new Object2ObjectOpenHashMap<>();
                for (int i = 0; i < this.getSlots(); i++) {
                    final ItemStack stack = this.getStackInSlot(i);
                    if (!stack.isEmpty()) {
                        totals.mergeInt(stack.getItem(), 1, Integer::sum);
                        stacks.putIfAbsent(stack.getItem(), stack);
                    }
                }
                final Iterator<Object2IntMap.Entry<Item>> topTotals = totals.object2IntEntrySet().stream()
                    .sorted(Comparator.<Object2IntMap.Entry<Item>>comparingInt(e -> e.getKey() instanceof BlockItem ? 0 : 1)
                        .thenComparingInt(e -> -e.getIntValue()))
                    .limit(CARGO.size()).iterator();
                final ItemStack[] items = new ItemStack[CARGO.size()];
                Arrays.fill(items, ItemStack.EMPTY);
                final int forth = this.getSlots() / CARGO.size();
                for (int pos = 0; topTotals.hasNext() && pos < CARGO.size(); ) {
                    final Object2IntMap.Entry<Item> entry = topTotals.next();
                    final int count = Math.max(1, (entry.getIntValue() + forth / 2) / forth);
                    for (int n = 1; n <= count && pos < CARGO.size(); n++) {
                        final ItemStack stack = stacks.getOrDefault(entry.getKey(), ItemStack.EMPTY).copy();
                        stack.setCount(Math.min(stack.getMaxStackSize(), entry.getIntValue() / n));
                        items[pos++] = stack;
                    }
                }
                for (int i = 0; i < CARGO.size(); i++) {
                    this.cart.getEntityData().set(CARGO.get(i), items[i]);
                }
            }
        };
    }

    @Override
    public InteractionResult interact(final Player player, final InteractionHand hand) {
        if (player.isSecondaryUseActive()) {
            this.openContainer(player);
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }
        final InteractionResult bannerResult = this.useBanner(player, hand);
        if (bannerResult.consumesAction()) {
            return bannerResult;
        }
        final ItemStack held = player.getItemInHand(hand);
        if (this.hasJukebox()) {
            if (this.level.isClientSide) return InteractionResult.SUCCESS;
            if (held.getItem() instanceof RecordItem && this.insertDisc(player, held) || this.ejectDisc(player)) {
                return InteractionResult.CONSUME;
            } else {
                return InteractionResult.FAIL;
            }
        }
        if (this.isVehicle()) {
            return InteractionResult.PASS;
        }
        if (!this.level.isClientSide) {
            return player.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
        }
        return InteractionResult.SUCCESS;
    }

    private boolean insertDisc(final Player player, final ItemStack held) {
        for (int i = 0; i < this.inventory.getSlots(); i++) {
            final ItemStack stack = this.inventory.getStackInSlot(i);
            if (DiscTag.insert(stack, held)) {
                this.inventory.setStackInSlot(i, stack);
                ((ServerLevel) this.level).getChunkSource().broadcastAndSend(this, new ClientboundSetEntityDataPacket(this.getId(), this.entityData, false));
                this.level.broadcastEntityEvent(this, (byte) 5);
                if (!player.getAbilities().instabuild) held.shrink(1);
                return true;
            }
        }
        return false;
    }

    private boolean ejectDisc(final Player player) {
        for (int i = 0; i < this.inventory.getSlots(); i++) {
            final ItemStack stack = this.inventory.getStackInSlot(i);
            final DiscTag record = DiscTag.get(stack);
            if (record.eject(player)) {
                this.inventory.setStackInSlot(i, stack);
                return true;
            }
        }
        return false;
    }

    public boolean hasJukebox() {
        for (final EntityDataAccessor<ItemStack> slot : CARGO) {
            final ItemStack cargo = this.entityData.get(slot);
            if (cargo.getItem() == Items.JUKEBOX) return true;
        }
        return false;
    }

    public ItemStack getDisc() {
        for (final EntityDataAccessor<ItemStack> slot : CARGO) {
            final ItemStack disc = DiscTag.get(this.entityData.get(slot)).disc;
            if (!disc.isEmpty()) return disc;
        }
        return ItemStack.EMPTY;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handleEntityEvent(final byte id) {
        if (id == 5) {
            for (final EntityDataAccessor<ItemStack> slot : CARGO) {
                final ItemStack disc = DiscTag.get(this.entityData.get(slot)).disc;
                if (!disc.isEmpty()) {
                    CartingJukeboxSound.play(this, disc);
                    break;
                }
            }
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public double getPassengersRidingOffset() {
        return 11.0D / 16.0D;
    }

    @Override
    public void positionRider(final Entity passenger) {
        if (this.hasPassenger(passenger)) {
            final Vec3 forward = this.getLookAngle();
            final Vec3 origin = new Vec3(0.0D, this.getPassengersRidingOffset(), 1.0D / 16.0D);
            final Vec3 pos = origin.add(forward.scale(-0.68D));
            passenger.setPos(this.getX() + pos.x, this.getY() + pos.y - 0.1D + passenger.getMyRidingOffset(), this.getZ() + pos.z);
            passenger.setYBodyRot(this.getYRot() + 180.0F);
            final float f2 = Mth.wrapDegrees(passenger.getYRot() - this.getYRot() + 180.0F);
            final float f1 = Mth.clamp(f2, -105.0F, 105.0F);
            passenger.yRotO += f1 - f2;
            passenger.setYRot(passenger.getYRot() + (f1 - f2));
            passenger.setYHeadRot(passenger.getYRot());
        }
    }

    public NonNullList<ItemStack> getCargo() {
        final NonNullList<ItemStack> cargo = NonNullList.withSize(CARGO.size(), ItemStack.EMPTY);
        for (int i = 0; i < CARGO.size(); i++) {
            cargo.set(i, this.entityData.get(CARGO.get(i)));
        }
        return cargo;
    }

    @Override
    public Item getCartItem() {
        return AstikorCarts.Items.SUPPLY_CART.get();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        for (final EntityDataAccessor<ItemStack> parameter : CARGO) {
            this.entityData.define(parameter, ItemStack.EMPTY);
        }
    }

    public void openContainer(final Player player) {
        if (!this.level.isClientSide) {
            player.openMenu(new SimpleMenuProvider((id, inv, plyr) -> {
                return new SupplyCartContainer(id, inv, this);
            }, this.getDisplayName()));
        }
    }

    static class DiscTag {
        static final DiscTag EMPTY = new DiscTag(ItemStack.EMPTY, new CompoundTag(), new CompoundTag(), ItemStack.EMPTY);

        final ItemStack stack;
        final CompoundTag nbt, tag;
        final ItemStack disc;

        DiscTag(final ItemStack stack, final CompoundTag nbt, final CompoundTag tag, final ItemStack disc) {
            this.stack = stack;
            this.nbt = nbt;
            this.tag = tag;
            this.disc = disc;
        }

        boolean isEmpty() {
            return this.stack.isEmpty();
        }

        boolean eject(final Player player) {
            if (this.isEmpty()) return false;
            this.tag.remove("RecordItem");
            if (this.tag.isEmpty()) this.nbt.remove("BlockEntityTag");
            if (this.nbt.contains("display", Tag.TAG_COMPOUND)) {
                final CompoundTag display = this.nbt.getCompound("display");
                if (display.contains("Lore", Tag.TAG_LIST)) {
                    final ListTag lore = display.getList("Lore", Tag.TAG_STRING);
                    final String descKey = this.disc.getItem().getDescriptionId() + ".desc";
                    for (int i = lore.size(); i --> 0; ) {
                        final String s = lore.getString(i);
                        final MutableComponent component;
                        try {
                            component = Component.Serializer.fromJson(s);
                        } catch (final JsonParseException ignored) {
                            continue;
                        }
                        if (component instanceof TranslatableComponent && descKey.equals(((TranslatableComponent) component).getKey())) {
                            lore.remove(i);
                        }
                    }
                }
            }
            if (this.nbt.isEmpty()) this.stack.setTag(null);
            ItemHandlerHelper.giveItemToPlayer(player, this.disc, player.getInventory().selected);
            return true;
        }

        static DiscTag get(final ItemStack stack) {
            if (stack.getItem() != Items.JUKEBOX) return EMPTY;
            final CompoundTag nbt = stack.getTag();
            if (nbt == null || !nbt.contains("BlockEntityTag", Tag.TAG_COMPOUND)) return EMPTY;
            final CompoundTag tag = nbt.getCompound("BlockEntityTag");
            if (!tag.contains("RecordItem", Tag.TAG_COMPOUND)) return EMPTY;
            return new DiscTag(stack, nbt, tag, ItemStack.of(tag.getCompound("RecordItem")));
        }

        static boolean insert(final ItemStack stack, final ItemStack disc) {
            if (stack.getItem() != Items.JUKEBOX) return false;
            final CompoundTag tag = stack.getOrCreateTagElement("BlockEntityTag");
            if (tag.contains("RecordItem", Tag.TAG_COMPOUND)) return false;
            tag.put("RecordItem", disc.save(new CompoundTag()));
            final CompoundTag display = stack.getOrCreateTagElement("display");
            final ListTag lore = display.getList("Lore", Tag.TAG_STRING);
            lore.add(StringTag.valueOf(Component.Serializer.toJson(new TranslatableComponent(disc.getDescriptionId() + ".desc"))));
            display.put("Lore", lore);
            return true;
        }
    }
}
