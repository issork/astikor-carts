package de.mennomax.horsecarts.item;

import de.mennomax.horsecarts.entity.EntityCargoCart;
import de.mennomax.horsecarts.entity.EntityCart;
import net.minecraft.world.World;

public class ItemCargoCart extends CartItem
{
    public ItemCargoCart()
    {
        super("cargocart");
    }

    @Override
    public EntityCart newCart(World worldIn)
    {
        return new EntityCargoCart(worldIn);
    }
}
