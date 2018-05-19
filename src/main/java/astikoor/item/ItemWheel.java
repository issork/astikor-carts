package astikoor.item;

import astikoor.Astikoor;
import astikoor.init.ModCreativeTabs;
import net.minecraft.item.Item;

public class ItemWheel extends Item
{
    public ItemWheel()
    {
        this.setRegistryName(Astikoor.MODID, "wheel");
        this.setUnlocalizedName(this.getRegistryName().toString());
        setCreativeTab(ModCreativeTabs.astikoor);
    }
}
