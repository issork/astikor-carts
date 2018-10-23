package de.mennomax.astikoorcarts;

import de.mennomax.astikoorcarts.capabilities.IPull;
import de.mennomax.astikoorcarts.capabilities.PullFactory;
import de.mennomax.astikoorcarts.capabilities.PullStorage;
import de.mennomax.astikoorcarts.handler.GuiHandler;
import de.mennomax.astikoorcarts.handler.MissingMappingHandler;
import de.mennomax.astikoorcarts.handler.PacketHandler;
import de.mennomax.astikoorcarts.handler.PersistanceHandler;
import de.mennomax.astikoorcarts.proxy.IProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
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
    public static final String VERSION = "1.12.2-0.1.2.0";

    @SidedProxy(clientSide = "de.mennomax.astikoorcarts.proxy.ClientProxy", serverSide = "de.mennomax.astikoorcarts.proxy.ServerProxy")
    public static IProxy proxy;

    @Instance(MODID)
    public static AstikoorCarts instance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        PacketHandler.registerPackets();
        CapabilityManager.INSTANCE.register(IPull.class, new PullStorage(), PullFactory::new);
        proxy.preInit();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(new MissingMappingHandler());
        MinecraftForge.EVENT_BUS.register(new PersistanceHandler());
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
        proxy.init();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        proxy.postInit();
    }
}
