package de.mennomax.astikoorcarts.proxy;

import de.mennomax.astikoorcarts.handler.ClientTickEventHandler;
import de.mennomax.astikoorcarts.handler.GuiEventHandler;
import de.mennomax.astikoorcarts.init.ModEntities;
import de.mennomax.astikoorcarts.init.ModKeybindings;
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
