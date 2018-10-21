package de.mennomax.astikoorcarts.item;

import de.mennomax.astikoorcarts.entity.AbstractDrawn;
import de.mennomax.astikoorcarts.entity.EntityCarriage;
import net.minecraft.world.World;

public class ItemCarriage extends CartItem
{
    public ItemCarriage()
    {
        super("carriage");
    }

    @Override
    public AbstractDrawn newCart(World worldIn)
    {
        return new EntityCarriage(worldIn);
    }
}
