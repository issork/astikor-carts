package astikoor;

import astikoor.handler.GuiHandler;
import astikoor.handler.PacketHandler;
import astikoor.handler.TrackingEventHandler;
import astikoor.init.ModEntities;
import astikoor.proxy.IProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

@Mod(modid = Astikoor.MODID, version = Astikoor.VERSION, acceptedMinecraftVersions = "[1.12,1.13)")
public class Astikoor
{
    public static final String MODID = "astikoor";
    public static final String VERSION = "1.1.1";

    @SidedProxy(clientSide = "astikoor.proxy.ClientProxy", serverSide = "astikoor.proxy.ServerProxy")
    public static IProxy proxy;

    @Instance(MODID)
    public static Astikoor instance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        ModEntities.registerEntities();
        PacketHandler.registerPackets();
        MinecraftForge.EVENT_BUS.register(new TrackingEventHandler());
        proxy.preInit();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
        proxy.init();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        proxy.postInit();
    }
}
