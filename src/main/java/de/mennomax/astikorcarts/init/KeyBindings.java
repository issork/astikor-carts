package de.mennomax.astikorcarts.init;

import org.lwjgl.glfw.GLFW;

import de.mennomax.astikorcarts.AstikorCarts;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = AstikorCarts.MODID, bus=Bus.MOD)
public class KeyBindings {

    public static final KeyBinding[] KEYBINDINGS = {
            new KeyBinding("key.astikorcarts.desc", GLFW.GLFW_KEY_R, "key.categories.astikorcarts")
    };

    @SubscribeEvent
    public static void registerKeyBindings(final FMLClientSetupEvent event) {
        for (KeyBinding bind : KEYBINDINGS) {
            ClientRegistry.registerKeyBinding(bind);
        }
    }

}
