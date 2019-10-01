package de.mennomax.astikorcarts.entity;

import de.mennomax.astikorcarts.init.Items;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CargoCartEntity extends AbstractDrawnInventoryEntity implements IInventoryChangedListener
{

    private static final DataParameter<Integer> CARGO = EntityDataManager.<Integer>createKey(CargoCartEntity.class, DataSerializers.VARINT);

    public CargoCartEntity(EntityType<? extends Entity> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
        this.initInventory(54);
        this.inventory.addListener(this);
    }
    
    @Override
    public double getMountedYOffset() {
        return 0.62D;
    }
    
    @Override
    public void updatePassenger(Entity passenger) {
        if (this.isPassenger(passenger))
        {
            Vec3d vec3d = (new Vec3d(-0.68D, 0.0D, 0.0D)).rotateYaw(-this.rotationYaw * 0.017453292F - ((float) Math.PI / 2F));
            passenger.setPosition(this.posX + vec3d.x, this.posY + this.getMountedYOffset() + passenger.getYOffset(), this.posZ + vec3d.z);
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
    
    @Override
    public void onInventoryChanged(IInventory invBasic)
    {
        if (!this.world.isRemote)
        {
            int tempload = 0;
            for (int i = 0; i < this.inventory.getSizeInventory(); i++)
            {
                if (!this.inventory.getStackInSlot(i).isEmpty())
                {
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
            if (this.dataManager.get(CARGO).intValue() != newValue)
            {
                this.dataManager.set(CARGO, newValue);
            }
        }
    }

}
