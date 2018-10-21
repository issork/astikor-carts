package de.mennomax.astikoorcarts.item;

import de.mennomax.astikoorcarts.entity.AbstractDrawn;
import de.mennomax.astikoorcarts.entity.EntityCargoCart;
import net.minecraft.world.World;

public class ItemCargoCart extends CartItem
{
    public ItemCargoCart()
    {
        super("cargocart");
    }

    @Override
    public AbstractDrawn newCart(World worldIn)
    {
        return new EntityCargoCart(worldIn);
    }
}
