package de.mennomax.astikorcarts.init;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class KeyBindings {

    public static final KeyBinding[] KEYBINDINGS = {
            new KeyBinding("key.astikorcarts.desc", GLFW.GLFW_KEY_R, "key.categories.astikorcarts")
    };

    public static void registerKeyBindings() {
        for (KeyBinding bind : KEYBINDINGS) {
            ClientRegistry.registerKeyBinding(bind);
        }
    }

}
