package de.mennomax.astikorcarts.entity;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public abstract class AbstractDrawnInventoryEntity extends AbstractDrawnEntity {
    
    public Inventory inventory;
    private LazyOptional<IItemHandler> itemHandler = null;
    
    public AbstractDrawnInventoryEntity(EntityType<? extends Entity> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }
    
    /**
     * Initializes an inventory with an IInventory wrapper.
     * @param size the size of the inventory.
     */
    protected void initInventory(int size) {
        this.inventory = new Inventory(size);
        this.itemHandler = LazyOptional.of(() -> new InvWrapper(this.inventory));
    }
    
    @Override
    public void onDestroyedAndDoDrops(DamageSource source) {
        InventoryHelper.dropInventoryItems(this.world, this, inventory);
    }
    
    @Override
    protected void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        ListNBT invCompound = compound.getList("Items", 10);
        for(int i = 0; i < invCompound.size(); i++) {
            CompoundNBT itemCompound = invCompound.getCompound(i);
            ItemStack itemstack = ItemStack.read(itemCompound);
            this.inventory.setInventorySlotContents(itemCompound.getByte("Slot") & 255, itemstack);
            System.out.println(itemstack + "READ");
        }
    }
    
    @Override
    protected void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        ListNBT invCompound = new ListNBT();
        for(int i = 0; i < this.inventory.getSizeInventory(); i++) {
            ItemStack itemstack = this.inventory.getStackInSlot(i);
            if(!itemstack.isEmpty()) {
                CompoundNBT itemCompound = new CompoundNBT();
                itemCompound.putByte("Slot", (byte) i);
                itemstack.write(itemCompound);
                invCompound.add(itemCompound);
            }
        }
        compound.put("Items", invCompound);
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
