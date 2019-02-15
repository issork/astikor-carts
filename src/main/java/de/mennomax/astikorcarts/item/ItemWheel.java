package de.mennomax.astikorcarts.item;

import de.mennomax.astikorcarts.AstikorCarts;
import de.mennomax.astikorcarts.init.ModCreativeTabs;
import net.minecraft.item.Item;

public class ItemWheel extends Item
{
    public ItemWheel()
    {
        this.setRegistryName(AstikorCarts.MODID, "wheel");
        this.setUnlocalizedName(this.getRegistryName().toString());
        this.setCreativeTab(ModCreativeTabs.astikor);
    }
}
