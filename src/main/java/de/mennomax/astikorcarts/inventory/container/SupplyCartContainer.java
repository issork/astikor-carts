package de.mennomax.astikorcarts.inventory.container;

import de.mennomax.astikorcarts.entity.AbstractDrawnInventoryEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.items.SlotItemHandler;

public final class SupplyCartContainer extends CartContainer {
    public SupplyCartContainer(final int id, final Inventory playerInv, final AbstractDrawnInventoryEntity cart) {
        super(MenuType.GENERIC_9x6, id, cart);
        final int upperInvHeight = 36;

        for (int i = 0; i < 6; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new SlotItemHandler(this.cartInv, j + i * 9, 8 + j * 18, 18 + i * 18));
            }
        }

        for (int k = 0; k < 3; ++k) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInv, l + k * 9 + 9, 8 + l * 18, 103 + k * 18 + upperInvHeight));
            }
        }

        for (int x = 0; x < 9; ++x) {
            this.addSlot(new Slot(playerInv, x, 8 + x * 18, 161 + upperInvHeight));
        }
    }
}
