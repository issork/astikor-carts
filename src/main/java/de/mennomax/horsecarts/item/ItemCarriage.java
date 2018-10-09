package de.mennomax.horsecarts.item;

import de.mennomax.horsecarts.entity.EntityCarriage;
import de.mennomax.horsecarts.entity.EntityCart;
import net.minecraft.world.World;

public class ItemCarriage extends CartItem
{
    public ItemCarriage()
    {
        super("carriage");
    }

    @Override
    public EntityCart newCart(World worldIn)
    {
        return new EntityCarriage(worldIn);
    }
}
