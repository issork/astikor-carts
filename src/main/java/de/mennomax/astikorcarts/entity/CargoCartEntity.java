package de.mennomax.astikorcarts.entity;

import de.mennomax.astikorcarts.AstikorCarts;
import de.mennomax.astikorcarts.config.AstikorCartsConfig;
import de.mennomax.astikorcarts.inventory.container.CargoCartContainer;
import de.mennomax.astikorcarts.util.CartItemStackHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;

public final class CargoCartEntity extends AbstractDrawnInventoryEntity {
    private static final DataParameter<Integer> CARGO = EntityDataManager.createKey(CargoCartEntity.class, DataSerializers.VARINT);

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
            protected void onContentsChanged(final int slot) {
                int tempload = 0;
                for (int i = 0; i < this.getSlots(); i++) {
                    if (!this.getStackInSlot(i).isEmpty()) {
                        tempload++;
                    }
                }
                final int newValue;
                if (tempload > 31)
                    newValue = 4;
                else if (tempload > 16)
                    newValue = 3;
                else if (tempload > 8)
                    newValue = 2;
                else if (tempload > 3)
                    newValue = 1;
                else
                    newValue = 0;
                this.cart.getDataManager().set(CARGO, newValue);
            }
        };
    }

    @Override
    public boolean processInitialInteract(final PlayerEntity player, final Hand hand) {
        if (!this.world.isRemote) {
            if (player.func_226563_dT_()) {
                this.openContainer(player);
            } else {
                player.startRiding(this);
            }
        }
        return true;
    }

    @Override
    public double getMountedYOffset() {
        return 11.0D / 16.0D;
    }

    @Override
    public void updatePassenger(final Entity passenger) {
        if (this.isPassenger(passenger)) {
            final Vec3d forward = this.getLookVec();
            final Vec3d origin = new Vec3d(0.0D, this.getMountedYOffset(), 1.0D / 16.0D);
            final Vec3d pos = origin.add(forward.scale(-0.68D));
            passenger.setPosition(this.getPosX() + pos.x, this.getPosY() + pos.y - 0.1D + passenger.getYOffset(), this.getPosZ() + pos.z);
            passenger.setRenderYawOffset(this.rotationYaw + 180.0F);
            final float f2 = MathHelper.wrapDegrees(passenger.rotationYaw - this.rotationYaw + 180.0F);
            final float f1 = MathHelper.clamp(f2, -105.0F, 105.0F);
            passenger.prevRotationYaw += f1 - f2;
            passenger.rotationYaw += f1 - f2;
            passenger.setRotationYawHead(passenger.rotationYaw);
        }
    }

    public int getCargo() {
        return this.dataManager.get(CARGO);
    }

    @Override
    public Item getCartItem() {
        return AstikorCarts.Items.CARGO_CART.get();
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(CARGO, 0);
    }

    @Override
    protected void readAdditional(final CompoundNBT compound) {
        super.readAdditional(compound);
        this.dataManager.set(CARGO, compound.getInt("Cargo"));
    }

    @Override
    protected void writeAdditional(final CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("Cargo", this.dataManager.get(CARGO));
    }

    public void openContainer(final PlayerEntity player) {
        player.openContainer(new SimpleNamedContainerProvider((id, inv, plyr) -> {
            return new CargoCartContainer(id, inv, this);
        }, this.getDisplayName()));
    }
}
