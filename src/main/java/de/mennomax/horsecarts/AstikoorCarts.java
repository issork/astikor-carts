package de.mennomax.horsecarts;

import de.mennomax.horsecarts.handler.GuiHandler;
import de.mennomax.horsecarts.handler.MissingMappingHandler;
import de.mennomax.horsecarts.handler.PacketHandler;
import de.mennomax.horsecarts.handler.TrackingEventHandler;
import de.mennomax.horsecarts.init.ModEntities;
import de.mennomax.horsecarts.proxy.IProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

@Mod(modid = AstikoorCarts.MODID, version = AstikoorCarts.VERSION, acceptedMinecraftVersions = "[1.12,1.13)")
public class AstikoorCarts
{
    public static final String MODID = "astikoorcarts";
    public static final String VERSION = "1.12-0.1.1.1";

    @SidedProxy(clientSide = "de.mennomax.horsecarts.proxy.ClientProxy", serverSide = "de.mennomax.horsecarts.proxy.ServerProxy")
    public static IProxy proxy;

    @Instance(MODID)
    public static AstikoorCarts instance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        ModEntities.registerEntities();
        PacketHandler.registerPackets();
        proxy.preInit();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	MinecraftForge.EVENT_BUS.register(new MissingMappingHandler());
    	MinecraftForge.EVENT_BUS.register(new TrackingEventHandler());
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
        proxy.init();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        proxy.postInit();
    }
}
