package astikoor.item;

import astikoor.entity.EntityCart;
import astikoor.entity.EntityPlowCart;
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
