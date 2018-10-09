package de.mennomax.horsecarts.item;

import de.mennomax.horsecarts.AstikoorCarts;
import de.mennomax.horsecarts.init.ModCreativeTabs;
import net.minecraft.item.Item;

public class ItemWheel extends Item
{
    public ItemWheel()
    {
        this.setRegistryName(AstikoorCarts.MODID, "wheel");
        this.setUnlocalizedName(this.getRegistryName().toString());
        setCreativeTab(ModCreativeTabs.astikoor);
    }
}
