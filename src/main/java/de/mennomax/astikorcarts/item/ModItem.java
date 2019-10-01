package de.mennomax.astikorcarts.item;

import de.mennomax.astikorcarts.AstikorCarts;
import net.minecraft.item.Item;

public class ModItem extends Item {

    public ModItem(Properties properties, String name) {
        super(properties);
        this.setRegistryName(AstikorCarts.MODID, name);
    }

}
