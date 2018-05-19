package astikoor.item;

import astikoor.entity.EntityCarriage;
import astikoor.entity.EntityCart;
import astikoor.entity.EntityChariot;
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
