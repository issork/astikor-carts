package de.mennomax.astikorcarts.util;

import de.mennomax.astikorcarts.entity.AbstractDrawnEntity;
import net.minecraftforge.items.ItemStackHandler;

public class CartItemHandler extends ItemStackHandler {
    
    protected final AbstractDrawnEntity CART;
    
    public CartItemHandler(int slots, AbstractDrawnEntity cart) {
        super(slots);
        this.CART = cart;
    }

}
