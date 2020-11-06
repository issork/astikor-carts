package de.mennomax.astikorcarts.entity;

import com.google.common.collect.ImmutableList;
import de.mennomax.astikorcarts.AstikorCarts;
import de.mennomax.astikorcarts.config.AstikorCartsConfig;
import de.mennomax.astikorcarts.inventory.container.CargoCartContainer;
import de.mennomax.astikorcarts.network.clientbound.CartingJukeboxMessage;
import de.mennomax.astikorcarts.util.CartItemStackHandler;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

public final class CargoCartEntity extends AbstractDrawnInventoryEntity {
    private static final ImmutableList<DataParameter<ItemStack>> CARGO = ImmutableList.of(
        EntityDataManager.createKey(CargoCartEntity.class, DataSerializers.ITEMSTACK),
        EntityDataManager.createKey(CargoCartEntity.class, DataSerializers.ITEMSTACK),
        EntityDataManager.createKey(CargoCartEntity.class, DataSerializers.ITEMSTACK),
        EntityDataManager.createKey(CargoCartEntity.class, DataSerializers.ITEMSTACK));

    public CargoCartEntity(final EntityType<? extends Entity> type, final World world) {
        super(type, world);
    }

    @Override
    protected ArrayList<String> getAllowedEntityList() {
        return AstikorCartsConfig.COMMON.cargoPullable.get();
    }

    @Override
    protected ItemStackHandler initInventory() {
        return new CartItemStackHandler<CargoCartEntity>(54, this) {
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
                    .sorted(Comparator.<Object2IntMap.Entry<Item>>comparingInt(e -> -e.getIntValue())
                        .thenComparingInt(e -> e.getKey() instanceof BlockItem ? 0 : 1))
                    .limit(CARGO.size()).iterator();
                final ItemStack[] items = new ItemStack[CARGO.size()];
                Arrays.fill(items, ItemStack.EMPTY);
                final int forth = this.getSlots() / CARGO.size();
                for (int pos = 0; topTotals.hasNext() && pos < CARGO.size(); ) {
                    final Object2IntMap.Entry<Item> entry = topTotals.next();
                    final int count = Math.max(1, (entry.getIntValue() + forth / 2) / forth);
                    for (int n = 0; n < count && pos < CARGO.size(); n++) {
                        items[pos++] = stacks.getOrDefault(entry.getKey(), ItemStack.EMPTY).copy();
                    }
                }
                for (int i = 0; i < CARGO.size(); i++) {
                    this.cart.getDataManager().set(CARGO.get(i), items[i]);
                }
            }
        };
    }

    @Override
    public ActionResultType processInitialInteract(final PlayerEntity player, final Hand hand) {
        final ItemStack held = player.getHeldItem(hand);
        if (held.getItem() instanceof MusicDiscItem && this.hasJukebox()) {
            if (this.world.isRemote) return ActionResultType.SUCCESS;
            if (this.jukebox(player, held)) return ActionResultType.CONSUME;
            return ActionResultType.FAIL;
        }
        if (player.isSecondaryUseActive()) {
            this.openContainer(player);
            return ActionResultType.func_233537_a_(this.world.isRemote);
        }
        if (this.isBeingRidden()) {
            return ActionResultType.PASS;
        }
        if (!this.world.isRemote) {
            return player.startRiding(this) ? ActionResultType.CONSUME : ActionResultType.PASS;
        }
        return ActionResultType.SUCCESS;
    }

    private boolean jukebox(final PlayerEntity player, final ItemStack held) {
        for (int i = 0; i < this.inventory.getSlots(); i++) {
            final ItemStack stack = this.inventory.getStackInSlot(i);
            if (stack.getItem() != Items.JUKEBOX) continue;
            final CompoundNBT tag = stack.getOrCreateChildTag("BlockEntityTag");
            if (tag.contains("RecordItem", Constants.NBT.TAG_COMPOUND)) continue;
            tag.put("RecordItem", held.write(new CompoundNBT()));
            final CompoundNBT display = stack.getOrCreateChildTag("display");
            final ListNBT lore = display.getList("Lore", Constants.NBT.TAG_STRING);
            lore.add(StringNBT.valueOf(ITextComponent.Serializer.toJson(new TranslationTextComponent(held.getTranslationKey() + ".desc"))));
            display.put("Lore", lore);
            AstikorCarts.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> this), new CartingJukeboxMessage(this, (MusicDiscItem) held.getItem()));
            if (!player.abilities.isCreativeMode) held.shrink(1);
            return true;
        }
        return false;
    }

    public boolean hasJukebox() {
        for (final DataParameter<ItemStack> slot : CARGO) {
            final ItemStack cargo = this.dataManager.get(slot);
            if (cargo.getItem() == Items.JUKEBOX) return true;
        }
        return false;
    }

    @Override
    public double getMountedYOffset() {
        return 11.0D / 16.0D;
    }

    @Override
    public void updatePassenger(final Entity passenger) {
        if (this.isPassenger(passenger)) {
            final Vector3d forward = this.getLookVec();
            final Vector3d origin = new Vector3d(0.0D, this.getMountedYOffset(), 1.0D / 16.0D);
            final Vector3d pos = origin.add(forward.scale(-0.68D));
            passenger.setPosition(this.getPosX() + pos.x, this.getPosY() + pos.y - 0.1D + passenger.getYOffset(), this.getPosZ() + pos.z);
            passenger.setRenderYawOffset(this.rotationYaw + 180.0F);
            final float f2 = MathHelper.wrapDegrees(passenger.rotationYaw - this.rotationYaw + 180.0F);
            final float f1 = MathHelper.clamp(f2, -105.0F, 105.0F);
            passenger.prevRotationYaw += f1 - f2;
            passenger.rotationYaw += f1 - f2;
            passenger.setRotationYawHead(passenger.rotationYaw);
        }
    }

    public NonNullList<ItemStack> getCargo() {
        final NonNullList<ItemStack> cargo = NonNullList.withSize(CARGO.size(), ItemStack.EMPTY);
        for (int i = 0; i < CARGO.size(); i++) {
            cargo.set(i, this.dataManager.get(CARGO.get(i)));
        }
        return cargo;
    }

    @Override
    public Item getCartItem() {
        return AstikorCarts.Items.CARGO_CART.get();
    }

    @Override
    protected void registerData() {
        super.registerData();
        for (final DataParameter<ItemStack> parameter : CARGO) {
            this.dataManager.register(parameter, ItemStack.EMPTY);
        }
    }

    public void openContainer(final PlayerEntity player) {
        if (!this.world.isRemote) {
            player.openContainer(new SimpleNamedContainerProvider((id, inv, plyr) -> {
                return new CargoCartContainer(id, inv, this);
            }, this.getDisplayName()));
        }
    }
}
