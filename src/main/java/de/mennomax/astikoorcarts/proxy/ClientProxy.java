package de.mennomax.astikoorcarts.proxy;

import de.mennomax.astikoorcarts.handler.ClientEventHandler;
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
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
        ModKeybindings.registerKeyBindings();
    }

    public void postInit()
    {

    }
}
