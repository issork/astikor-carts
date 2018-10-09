package de.mennomax.horsecarts.item;

import de.mennomax.horsecarts.entity.EntityCart;
import de.mennomax.horsecarts.entity.EntityChariot;
import net.minecraft.world.World;

public class ItemChariot extends CartItem
{
    public ItemChariot()
    {
        super("chariot");
    }

    @Override
    public EntityCart newCart(World worldIn)
    {
        return new EntityChariot(worldIn);
    }
}
