package de.mennomax.astikorcarts.init;

import de.mennomax.astikorcarts.AstikorCarts;
import de.mennomax.astikorcarts.client.gui.screen.inventory.PlowScreen;
import de.mennomax.astikorcarts.inventory.container.CartContainer;
import de.mennomax.astikorcarts.inventory.container.PlowCartContainer;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(AstikorCarts.MODID)
@EventBusSubscriber(modid = AstikorCarts.MODID, bus = Bus.MOD)
public class Inventories {

    public static final ContainerType<PlowCartContainer> PLOWCARTCONTAINER = null;

    @SubscribeEvent
    public static void clientSetup(final FMLClientSetupEvent event) {
        ScreenManager.registerFactory(PLOWCARTCONTAINER, PlowScreen::new);
    }

    @SubscribeEvent
    public static void registerContainerTypes(final RegistryEvent.Register<ContainerType<?>> event) {
        event.getRegistry().registerAll(
            createType(PlowCartContainer::new, "plowcartcontainer"));
    }

    private static ContainerType<?> createType(final IContainerFactory<? extends CartContainer> container, final String name) {
        return IForgeContainerType.create(container::create).setRegistryName(AstikorCarts.MODID, name);
    }

}
