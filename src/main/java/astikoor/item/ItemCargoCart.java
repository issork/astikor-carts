package astikoor.item;

import astikoor.entity.EntityCargoCart;
import astikoor.entity.EntityCart;
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
