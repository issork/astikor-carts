package de.mennomax.astikorcarts.entity;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public abstract class AbstractDrawnInventoryEntity extends AbstractDrawnEntity {
    
    public ItemStackHandler inventory = this.initInventory();
    private LazyOptional<ItemStackHandler> itemHandler = LazyOptional.of(() -> this.inventory);
    
    public AbstractDrawnInventoryEntity(EntityType<? extends Entity> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }
    
    protected abstract ItemStackHandler initInventory();
    
    @Override
    public boolean replaceItemInInventory(int inventorySlot, ItemStack itemStackIn) {
        if (inventorySlot >= 0 && inventorySlot < this.inventory.getSlots()) {
           this.inventory.setStackInSlot(inventorySlot, itemStackIn);
           return true;
        } else {
           return false;
        }
     }
    
    @Override
    public void onDestroyedAndDoDrops(DamageSource source) {
        for(int i = 0; i < inventory.getSlots(); i++) {
            InventoryHelper.spawnItemStack(this.world, this.posX, this.posY, this.posZ, inventory.getStackInSlot(i));
        }
    }
    
    @Override
    protected void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        inventory.deserializeNBT(compound.getCompound("Items"));
    }
    
    @Override
    protected void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.put("Items", inventory.serializeNBT());
    }
    
    @Override
    public void remove(boolean keepData) {
       super.remove(keepData);
       if (!keepData && itemHandler != null) {
          itemHandler.invalidate();
          itemHandler = null;
       }
    }
    
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
       if (this.isAlive() && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && itemHandler != null)
          return itemHandler.cast();
       return super.getCapability(capability, facing);
    }
    
}
