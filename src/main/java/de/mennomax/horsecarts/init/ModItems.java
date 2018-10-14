package de.mennomax.horsecarts.init;

import java.util.HashSet;
import java.util.Set;

import de.mennomax.horsecarts.AstikoorCarts;
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
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(AstikoorCarts.MODID)
public class ModItems
{
    public static final Item CARGOCART = null;
    public static final Item WHEEL = null;
    public static final Item PLOWCART = null;
    public static final Item CHARIOT = null;
    public static final Item CARRIAGE = null;

    @EventBusSubscriber
    public static class ItemRegistrationHandler
    {
        public static final Set<Item> ITEMSET = new HashSet<Item>();

        @SubscribeEvent
        public static void registerItems(final RegistryEvent.Register<Item> event)
        {
            final Item[] items = {
                    new ItemCargoCart(),
                    new ItemWheel(),
                    new ItemPlowCart(),
                    new ItemChariot(),
                    new ItemCarriage()
            };

            for (Item item : items)
            {
                event.getRegistry().register(item);
                ITEMSET.add(item);
            }
        }

        @SubscribeEvent
        public static void registerItemModels(final ModelRegistryEvent event)
        {
            for (Item item : ITEMSET)
            {
                ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
            }
        }
    }
}
