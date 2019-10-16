package de.mennomax.astikorcarts.entity;

import java.util.ArrayList;

import de.mennomax.astikorcarts.config.AstikorCartsConfig;
import de.mennomax.astikorcarts.init.Items;
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

public class CargoCartEntity extends AbstractDrawnInventoryEntity {

    private static final DataParameter<Integer> CARGO = EntityDataManager.<Integer>createKey(CargoCartEntity.class, DataSerializers.VARINT);

    public CargoCartEntity(EntityType<? extends Entity> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    protected ArrayList<String> getAllowedEntityList() {
        return AstikorCartsConfig.COMMON.CARGOCART.get();
    }

    @Override
    protected ItemStackHandler initInventory() {
        return new CartItemStackHandler<CargoCartEntity>(54, this) {
            @Override
            protected void onContentsChanged(int slot) {
                int tempload = 0;
                for (int i = 0; i < this.getSlots(); i++) {
                    if (!this.getStackInSlot(i).isEmpty()) {
                        tempload++;
                    }
                }
                int newValue;
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
                if (CART.getDataManager().get(CARGO).intValue() != newValue) {
                    CART.getDataManager().set(CARGO, newValue);
                }
            }
        };
    }

    @Override
    public boolean processInitialInteract(PlayerEntity player, Hand hand) {
        if (!this.world.isRemote) {
            if (player.isSneaking()) {
                this.openContainer(player);
            } else {
                player.startRiding(this);
            }
        }
        return true;
    }

    @Override
    public double getMountedYOffset() {
        return 0.56D;
    }

    @Override
    public void updatePassenger(Entity passenger) {
        if (this.isPassenger(passenger)) {
            Vec3d vec3d = (new Vec3d(-0.68D, 0.0D, 0.0D)).rotateYaw(-this.rotationYaw * 0.017453292F - ((float) Math.PI / 2F));
            passenger.setPosition(this.posX + vec3d.x, this.posY + this.getMountedYOffset() + passenger.getYOffset(), this.posZ + vec3d.z);
            passenger.setRenderYawOffset(this.rotationYaw + 180.0F);
            float f2 = MathHelper.wrapDegrees(passenger.rotationYaw - this.rotationYaw + 180.0F);
            float f1 = MathHelper.clamp(f2, -105.0F, 105.0F);
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
        return Items.CARGOCART;
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(CARGO, 0);
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        dataManager.set(CARGO, compound.getInt("Cargo"));
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("Cargo", dataManager.get(CARGO));
    }

    public void openContainer(PlayerEntity player) {
        player.openContainer(new SimpleNamedContainerProvider((id, inv, plyr) -> {
            return new CargoCartContainer(id, inv, this);
        }, this.getDisplayName()));
    }

}
