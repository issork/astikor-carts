package de.mennomax.astikorcarts.init;

import java.util.HashSet;
import java.util.Set;

import de.mennomax.astikorcarts.AstikorCarts;
import de.mennomax.astikorcarts.item.ItemCargoCart;
import de.mennomax.astikorcarts.item.ItemMobCart;
import de.mennomax.astikorcarts.item.ItemPlowCart;
import de.mennomax.astikorcarts.item.ItemWheel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(AstikorCarts.MODID)
public class ModItems
{
    public static final Item WHEEL = null;
    public static final Item CARGOCART = null;
    public static final Item PLOWCART = null;
    public static final Item MOBCART = null;

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
                    new ItemPlowCart(),
                    new ItemMobCart()
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
