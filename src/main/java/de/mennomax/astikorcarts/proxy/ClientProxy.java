package de.mennomax.astikorcarts.proxy;

import de.mennomax.astikorcarts.handler.ClientEventHandler;
import de.mennomax.astikorcarts.init.ModEntities;
import de.mennomax.astikorcarts.init.ModKeybindings;
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
