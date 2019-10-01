package de.mennomax.astikorcarts.init;

import de.mennomax.astikorcarts.AstikorCarts;
import de.mennomax.astikorcarts.item.CartItem;
import de.mennomax.astikorcarts.item.ModItem;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Properties;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(AstikorCarts.MODID)
@EventBusSubscriber(bus = Bus.MOD, modid = AstikorCarts.MODID)
public class Items {

    public static final Item CARGOCART = null;
    public static final Item PLOWCART = null;
    public static final Item MOBCART = null;

    @SubscribeEvent
    public static void registerItems(final RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                new ModItem(new Properties().group(ItemGroup.MATERIALS), "wheel"),
                new CartItem("cargocart"),
                new CartItem("plowcart"),
                new CartItem("mobcart"));
    }

}
