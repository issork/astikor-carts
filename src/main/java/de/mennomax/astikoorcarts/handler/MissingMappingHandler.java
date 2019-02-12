package de.mennomax.astikoorcarts.handler;

import de.mennomax.astikoorcarts.entity.EntityCargoCart;
import de.mennomax.astikoorcarts.entity.EntityPlowCart;
import de.mennomax.astikoorcarts.init.ModItems;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.RegistryEvent.MissingMappings.Mapping;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

/**
 * Will be removed in 1.13, only here to apply the new modid to existing worlds.
 *
 */
public class MissingMappingHandler
{

    @SubscribeEvent
    public void onMissingItemMapping(RegistryEvent.MissingMappings<Item> event)
    {
        for (Mapping<Item> mapping : event.getAllMappings())
        {
            switch (mapping.key.toString())
            {
            case "astikoor:plowcart":
                mapping.remap(ModItems.PLOWCART);
                break;
            case "astikoor:cargocart":
                mapping.remap(ModItems.CARGOCART);
                break;
            case "astikoor:wheel":
                mapping.remap(ModItems.WHEEL);
                break;
            }
        }
    }

    @SubscribeEvent
    public void onMissingEntityMapping(RegistryEvent.MissingMappings<EntityEntry> event)
    {
        for (Mapping<EntityEntry> mapping : event.getAllMappings())
        {
            switch (mapping.key.toString())
            {
            case "astikoor:plowcart":
                mapping.remap(EntityRegistry.getEntry(EntityPlowCart.class));
                break;
            case "astikoor:cargocart":
                mapping.remap(EntityRegistry.getEntry(EntityCargoCart.class));
                break;
            }
        }
    }
}
