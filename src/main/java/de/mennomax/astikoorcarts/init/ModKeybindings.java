package de.mennomax.astikoorcarts.init;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ModKeybindings
{
    public static List<KeyBinding> keybindings = new ArrayList<KeyBinding>();

    public static void registerKeyBindings()
    {
        keybindings.add(new KeyBinding("key.astikoorcarts.desc", Keyboard.KEY_R, "key.categories.astikoorcarts"));

        for (KeyBinding bind : keybindings)
        {
            ClientRegistry.registerKeyBinding(bind);
        }
    }

}
