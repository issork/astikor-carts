package de.mennomax.astikorcarts.entity;

import com.google.common.collect.ImmutableList;
import de.mennomax.astikorcarts.AstikorCarts;
import de.mennomax.astikorcarts.config.AstikorCartsConfig;
import de.mennomax.astikorcarts.inventory.container.CargoCartContainer;
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
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
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
        if (!this.world.isRemote) {
            if (player.isSneaking()) {
                this.openContainer(player);
            } else {
                player.startRiding(this);
            }
        }
        return ActionResultType.SUCCESS;
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
        player.openContainer(new SimpleNamedContainerProvider((id, inv, plyr) -> {
            return new CargoCartContainer(id, inv, this);
        }, this.getDisplayName()));
    }
}
