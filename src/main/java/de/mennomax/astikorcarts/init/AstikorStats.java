package de.mennomax.astikorcarts.init;

import de.mennomax.astikorcarts.AstikorCarts;
import net.minecraft.stats.IStatFormatter;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = AstikorCarts.MODID)
public final class AstikorStats {
    private AstikorStats() {}

    public static final ResourceLocation CART_ONE_CM = new ResourceLocation(AstikorCarts.MODID, "cart_one_cm");

    @SubscribeEvent
    public static void onRegister(final RegistryEvent.Register<StatType<?>> event) {
        registerCustom(CART_ONE_CM, IStatFormatter.DISTANCE);
    }

    private static void registerCustom(final ResourceLocation key, final IStatFormatter formatter) {
        Stats.CUSTOM.get(Registry.register(Registry.CUSTOM_STAT, key, key), formatter);
    }
}
