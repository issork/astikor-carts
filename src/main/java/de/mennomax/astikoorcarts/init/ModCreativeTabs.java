package de.mennomax.astikoorcarts.init;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class ModCreativeTabs
{
    public static CreativeTabs astikoor = new CreativeTabs("astikoorcarts")
    {
        @Override
        public ItemStack getTabIconItem()
        {
            return new ItemStack(ModItems.WHEEL);
        }
    };
}
