package de.mennomax.astikorcarts.proxy;

import de.mennomax.astikorcarts.init.ModEntities;
import de.mennomax.astikorcarts.init.ModKeybindings;

public class ClientProxy implements IProxy
{
    public void preInit()
    {
        ModEntities.registerRenders();
    }

    public void init()
    {
        ModKeybindings.registerKeyBindings();
    }

    public void postInit()
    {

    }
}
