package de.mennomax.astikorcarts.inventory.container;

import de.mennomax.astikorcarts.AstikorCarts;
import de.mennomax.astikorcarts.entity.AbstractDrawnInventoryEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public final class PlowContainer extends CartContainer {
    public PlowContainer(final int id, final Inventory playerInv, final FriendlyByteBuf buf) {
        this(id, playerInv, (AbstractDrawnInventoryEntity) playerInv.player.level.getEntity(buf.readInt()));
    }

    public PlowContainer(final int id, final Inventory playerInv, final AbstractDrawnInventoryEntity cart) {
        super(AstikorCarts.ContainerTypes.PLOW_CART.get(), id, cart);
        this.addSlot(new PlowSlot(this.cartInv, 0, 57, 24));
        this.addSlot(new PlowSlot(this.cartInv, 1, 80, 17));
        this.addSlot(new PlowSlot(this.cartInv, 2, 103, 24));

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInv, k, 8 + k * 18, 142));
        }
    }

    static class PlowSlot extends SlotItemHandler {
        public PlowSlot(final IItemHandler itemHandler, final int index, final int xPosition, final int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }
    }
}
