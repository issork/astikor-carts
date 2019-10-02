package de.mennomax.astikorcarts.inventory.container;

import de.mennomax.astikorcarts.entity.AbstractDrawnInventoryEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public abstract class CartContainer extends Container {

    protected final IInventory cartInv;
    protected final AbstractDrawnInventoryEntity cart;

    public CartContainer(ContainerType<?> type, int id, PlayerInventory playerInv, AbstractDrawnInventoryEntity cart) {
        super(type, id);
        this.cart = cart;
        this.cartInv = cart.inventory;
        cartInv.openInventory(playerInv.player);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return cartInv.isUsableByPlayer(playerIn) && this.cart.isAlive() && this.cart.getDistance(playerIn) < 8.0F;
    }
    
    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if(slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            if(index < cartInv.getSizeInventory()) {
                if (!this.mergeItemStack(itemstack1, cartInv.getSizeInventory(), this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if(!this.mergeItemStack(itemstack1, 0, cartInv.getSizeInventory(), false)) {
                return ItemStack.EMPTY;
            }
            if(itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }
        return itemstack;
    }
    
    @Override
    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        this.cartInv.closeInventory(playerIn);
    }

}
