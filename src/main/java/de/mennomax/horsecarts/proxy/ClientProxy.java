package de.mennomax.horsecarts.proxy;

import de.mennomax.horsecarts.handler.ClientTickEventHandler;
import de.mennomax.horsecarts.handler.GuiEventHandler;
import de.mennomax.horsecarts.init.ModEntities;
import de.mennomax.horsecarts.init.ModKeybindings;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy implements IProxy
{
    public void preInit()
    {
        ModEntities.registerRenders();
    }

    public void init()
    {
        MinecraftForge.EVENT_BUS.register(new GuiEventHandler());
        MinecraftForge.EVENT_BUS.register(new ClientTickEventHandler());
        ModKeybindings.registerKeyBindings();
    }

    public void postInit()
    {

    }
}
