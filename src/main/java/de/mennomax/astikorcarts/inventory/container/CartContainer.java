package de.mennomax.astikorcarts.inventory.container;

import de.mennomax.astikorcarts.entity.AbstractDrawnInventoryEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public abstract class CartContainer extends Container {

    protected final ItemStackHandler cartInv;
    protected final AbstractDrawnInventoryEntity cart;

    public CartContainer(final ContainerType<?> type, final int id, final AbstractDrawnInventoryEntity cart) {
        super(type, id);
        this.cart = cart;
        this.cartInv = cart.inventory;
    }

    @Override
    public boolean canInteractWith(final PlayerEntity playerIn) {
        return this.cart.isAlive() && this.cart.getDistance(playerIn) < 8.0F;
    }

    @Override
    public ItemStack transferStackInSlot(final PlayerEntity playerIn, final int index) {
        final ItemStack itemstack = ItemStack.EMPTY;
        final Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            final ItemStack itemstack1 = slot.getStack();
            if (index < this.cartInv.getSlots()) {
                if (!this.mergeItemStack(itemstack1, this.cartInv.getSlots(), this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, this.cartInv.getSlots(), false)) {
                return ItemStack.EMPTY;
            }
            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }
        return itemstack;
    }

    @Override
    public void onContainerClosed(final PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
    }

}
