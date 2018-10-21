package de.mennomax.astikoorcarts.handler;

import de.mennomax.astikoorcarts.entity.EntityCargoCart;
import de.mennomax.astikoorcarts.entity.EntityCarriage;
import de.mennomax.astikoorcarts.entity.EntityChariot;
import de.mennomax.astikoorcarts.entity.EntityPlowCart;
import de.mennomax.astikoorcarts.init.ModItems;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.RegistryEvent.MissingMappings.Mapping;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class MissingMappingHandler
{

    @SubscribeEvent
    public void onMissingItemMapping(RegistryEvent.MissingMappings<Item> event)
    {
        for (Mapping<Item> mapping : event.getAllMappings())
        {
            switch (mapping.key.toString())
            {
            case "astikoor:chariot":
                mapping.remap(ModItems.CHARIOT);
                break;
            case "astikoor:plowcart":
                mapping.remap(ModItems.PLOWCART);
                break;
            case "astikoor:cargocart":
                mapping.remap(ModItems.CARGOCART);
                break;
            case "astikoor:wheel":
                mapping.remap(ModItems.WHEEL);
                break;
            case "astikoor:carriage":
                mapping.remap(ModItems.CARRIAGE);
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
            case "astikoor:chariot":
                mapping.remap(EntityRegistry.getEntry(EntityChariot.class));
                break;
            case "astikoor:plowcart":
                mapping.remap(EntityRegistry.getEntry(EntityPlowCart.class));
                break;
            case "astikoor:cargocart":
                mapping.remap(EntityRegistry.getEntry(EntityCargoCart.class));
                break;
            case "astikoor:carriage":
                mapping.remap(EntityRegistry.getEntry(EntityCarriage.class));
                break;
            }
        }
    }
}
