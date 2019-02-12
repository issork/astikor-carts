package de.mennomax.astikoorcarts.item;

import de.mennomax.astikoorcarts.AstikoorCarts;
import de.mennomax.astikoorcarts.init.ModCreativeTabs;
import net.minecraft.item.Item;

public class ItemWheel extends Item
{
    public ItemWheel()
    {
        this.setRegistryName(AstikoorCarts.MODID, "wheel");
        this.setUnlocalizedName(this.getRegistryName().toString());
        this.setCreativeTab(ModCreativeTabs.astikoor);
    }
}
