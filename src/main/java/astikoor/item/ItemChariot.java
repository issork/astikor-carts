package astikoor.item;

import astikoor.entity.EntityCart;
import astikoor.entity.EntityChariot;
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
