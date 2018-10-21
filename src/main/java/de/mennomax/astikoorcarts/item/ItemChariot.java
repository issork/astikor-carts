package de.mennomax.astikoorcarts.item;

import de.mennomax.astikoorcarts.entity.AbstractDrawn;
import de.mennomax.astikoorcarts.entity.EntityChariot;
import net.minecraft.world.World;

public class ItemChariot extends CartItem
{
    public ItemChariot()
    {
        super("chariot");
    }

    @Override
    public AbstractDrawn newCart(World worldIn)
    {
        return new EntityChariot(worldIn);
    }
}
