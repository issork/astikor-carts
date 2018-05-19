package astikoor.init;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ModCreativeTabs
{
    public static CreativeTabs astikoor = new CreativeTabs("astikoor")
    {
        @Override
        public ItemStack getTabIconItem()
        {
            return new ItemStack(Items.MAP);
        }
    };
}
