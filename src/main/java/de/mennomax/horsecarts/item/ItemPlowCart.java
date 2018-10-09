package de.mennomax.horsecarts.item;

import de.mennomax.horsecarts.entity.EntityCart;
import de.mennomax.horsecarts.entity.EntityPlowCart;
import net.minecraft.world.World;

public class ItemPlowCart extends CartItem
{
    public ItemPlowCart()
    {
        super("plowcart");
    }

    @Override
    public EntityCart newCart(World worldIn)
    {
        return new EntityPlowCart(worldIn);
    }
}
