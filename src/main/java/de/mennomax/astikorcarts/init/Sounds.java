package de.mennomax.astikorcarts.init;

import de.mennomax.astikorcarts.AstikorCarts;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = AstikorCarts.MODID)
public final class Sounds {
    private Sounds() {}

    public static SoundEvent CART_ATTACHED = create("cart.attached");

    public static SoundEvent CART_DETACHED = create("cart.detached");

    @SubscribeEvent
    public static void onRegister(final RegistryEvent.Register<SoundEvent> event) {
        event.getRegistry().registerAll(CART_ATTACHED, CART_DETACHED);
    }

    private static SoundEvent create(final String name) {
        return new SoundEvent(new ResourceLocation(AstikorCarts.MODID, name)).setRegistryName(new ResourceLocation(AstikorCarts.MODID, name));
    }
}
