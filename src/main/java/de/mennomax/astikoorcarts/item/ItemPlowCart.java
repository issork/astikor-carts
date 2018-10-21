package de.mennomax.astikoorcarts.item;

import de.mennomax.astikoorcarts.entity.AbstractDrawn;
import de.mennomax.astikoorcarts.entity.EntityPlowCart;
import net.minecraft.world.World;

public class ItemPlowCart extends CartItem
{
    public ItemPlowCart()
    {
        super("plowcart");
    }

    @Override
    public AbstractDrawn newCart(World worldIn)
    {
        return new EntityPlowCart(worldIn);
    }
}
