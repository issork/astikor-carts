package de.mennomax.astikorcarts.inventory.container;

import de.mennomax.astikorcarts.entity.AbstractDrawnInventoryEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public abstract class CartContainer extends AbstractContainerMenu {
    protected final ItemStackHandler cartInv;

    protected final AbstractDrawnInventoryEntity cart;

    public CartContainer(final MenuType<?> type, final int id, final AbstractDrawnInventoryEntity cart) {
        super(type, id);
        this.cart = cart;
        this.cartInv = cart.inventory;
    }

    @Override
    public boolean stillValid(final Player playerIn) {
        return this.cart.isAlive() && this.cart.distanceTo(playerIn) < 8.0F;
    }

    @Override
    public ItemStack quickMoveStack(final Player playerIn, final int index) {
        final ItemStack itemstack = ItemStack.EMPTY;
        final Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            final ItemStack itemstack1 = slot.getItem();
            if (index < this.cartInv.getSlots()) {
                if (!this.moveItemStackTo(itemstack1, this.cartInv.getSlots(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, this.cartInv.getSlots(), false)) {
                return ItemStack.EMPTY;
            }
            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }
}
