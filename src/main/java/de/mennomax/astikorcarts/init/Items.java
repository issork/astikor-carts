package de.mennomax.astikorcarts.init;

import de.mennomax.astikorcarts.AstikorCarts;
import de.mennomax.astikorcarts.item.CartItem;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Properties;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(AstikorCarts.ID)
@EventBusSubscriber(bus = Bus.MOD, modid = AstikorCarts.ID)
public class Items {

    public static final Item CARGOCART = null;
    public static final Item PLOWCART = null;
    public static final Item MOBCART = null;

    @SubscribeEvent
    public static void registerItems(final RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
            new Item(new Properties().group(ItemGroup.MATERIALS)).setRegistryName("wheel"),
            createCart("cargocart"),
            createCart("plowcart"),
            createCart("mobcart")
        );
    }

    private static Item createCart(final String name) {
        return new CartItem(new Properties().maxStackSize(1).group(ItemGroup.TRANSPORTATION)).setRegistryName(name);
    }
}
