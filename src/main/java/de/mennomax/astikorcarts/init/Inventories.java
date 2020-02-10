package de.mennomax.astikorcarts.init;

import de.mennomax.astikorcarts.AstikorCarts;
import de.mennomax.astikorcarts.inventory.container.CartContainer;
import de.mennomax.astikorcarts.inventory.container.PlowCartContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(AstikorCarts.ID)
@EventBusSubscriber(modid = AstikorCarts.ID, bus = Bus.MOD)
public class Inventories {

    public static final ContainerType<PlowCartContainer> PLOWCARTCONTAINER = null;

    @SubscribeEvent
    public static void registerContainerTypes(final RegistryEvent.Register<ContainerType<?>> event) {
        event.getRegistry().registerAll(
            createType(PlowCartContainer::new, "plowcartcontainer"));
    }

    private static ContainerType<?> createType(final IContainerFactory<? extends CartContainer> container, final String name) {
        return IForgeContainerType.create(container).setRegistryName(AstikorCarts.ID, name);
    }

}
