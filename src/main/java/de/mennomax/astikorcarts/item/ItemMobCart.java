package de.mennomax.astikorcarts.item;

import de.mennomax.astikorcarts.entity.AbstractDrawn;
import de.mennomax.astikorcarts.entity.EntityMobCart;
import net.minecraft.world.World;

public class ItemMobCart extends AbstractCartItem
{
    public ItemMobCart()
    {
        super("mobcart");
    }

    @Override
    public AbstractDrawn newCart(World worldIn)
    {
        return new EntityMobCart(worldIn);
    }

}
