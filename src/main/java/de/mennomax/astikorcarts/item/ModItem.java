package de.mennomax.astikorcarts.item;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class ModItem extends Item {

    public ModItem(final Properties properties, final ResourceLocation name) {
        super(properties);
        this.setRegistryName(name);
    }

}
