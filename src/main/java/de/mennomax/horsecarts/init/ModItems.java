package de.mennomax.horsecarts.init;

import java.util.HashSet;
import java.util.Set;

import de.mennomax.horsecarts.item.ItemCargoCart;
import de.mennomax.horsecarts.item.ItemCarriage;
import de.mennomax.horsecarts.item.ItemChariot;
import de.mennomax.horsecarts.item.ItemPlowCart;
import de.mennomax.horsecarts.item.ItemWheel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ModItems
{
    public static final Item cargocart = new ItemCargoCart();
    public static final Item wheel = new ItemWheel();
    public static final Item plowcart = new ItemPlowCart();
    public static final Item chariot = new ItemChariot();
    public static final Item carriage = new ItemCarriage();

    public static Set<Item> itemset;

    @EventBusSubscriber
    public static class ItemRegistrationHandler
    {
        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event)
        {
            itemset = new HashSet<Item>();

            itemset.add(cargocart);
            itemset.add(wheel);
            itemset.add(plowcart);
            itemset.add(chariot);
            itemset.add(carriage);

            for(Item item : itemset)
            {
                event.getRegistry().register(item);
            }	
        }

        @SubscribeEvent
        public static void registerItemModels(ModelRegistryEvent event)
        {
            for(Item item : itemset)
            {
                ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
            }
        }
    }
}
