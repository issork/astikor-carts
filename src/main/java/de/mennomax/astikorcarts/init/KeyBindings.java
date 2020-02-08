package de.mennomax.astikorcarts.init;

import de.mennomax.astikorcarts.AstikorCarts;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = AstikorCarts.MODID, bus = Bus.MOD, value = Dist.CLIENT)
public final class KeyBindings {
    public static final KeyBinding ACTION = new KeyBinding("key.astikorcarts.desc", GLFW.GLFW_KEY_R, "key.categories.astikorcarts");

    @SubscribeEvent
    public static void registerKeyBindings(final FMLClientSetupEvent event) {
        ClientRegistry.registerKeyBinding(ACTION);
    }
}
