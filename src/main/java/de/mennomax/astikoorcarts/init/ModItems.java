package de.mennomax.astikoorcarts.init;

import java.util.HashSet;
import java.util.Set;

import de.mennomax.astikoorcarts.AstikoorCarts;
import de.mennomax.astikoorcarts.item.ItemCargoCart;
import de.mennomax.astikoorcarts.item.ItemPlowCart;
import de.mennomax.astikoorcarts.item.ItemWheel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(AstikoorCarts.MODID)
public class ModItems
{
    public static final Item WHEEL = null;
    public static final Item CARGOCART = null;
    public static final Item PLOWCART = null;

    @EventBusSubscriber
    public static class ItemRegistrationHandler
    {
        public static final Set<Item> ITEMSET = new HashSet<Item>();

        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event)
        {
            final Item[] ITEMS = {
                    new ItemWheel(),
                    new ItemCargoCart(),
                    new ItemPlowCart()
            };

            for (Item item : ITEMS)
            {
                event.getRegistry().register(item);
                ITEMSET.add(item);
            }
        }

        @SubscribeEvent
        public static void registerItemModels(ModelRegistryEvent event)
        {
            for (Item item : ITEMSET)
            {
                ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
            }
        }
    }
}
