package de.mennomax.astikorcarts.util;

import de.mennomax.astikorcarts.entity.AbstractDrawnEntity;
import net.minecraftforge.items.ItemStackHandler;

public class CartItemStackHandler<T extends AbstractDrawnEntity> extends ItemStackHandler {

    protected final T CART;

    public CartItemStackHandler(int slots, T cart) {
        super(slots);
        this.CART = cart;
    }

}
