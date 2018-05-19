package astikoor.proxy;

import astikoor.handler.ClientTickEventHandler;
import astikoor.init.ModEntities;
import astikoor.init.ModKeybindings;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy implements IProxy
{
    public void preInit()
    {
        ModEntities.registerRenders();
        MinecraftForge.EVENT_BUS.register(new ClientTickEventHandler());
    }

    public void init()
    {
        ModKeybindings.registerKeyBindings();
    }

    public void postInit()
    {

    }
}
