package de.mennomax.astikoorcarts.handler;

import de.mennomax.astikoorcarts.AstikoorCarts;
import de.mennomax.astikoorcarts.packets.CPacketActionKey;
import de.mennomax.astikoorcarts.packets.CPacketOpenCartGui;
import de.mennomax.astikoorcarts.packets.CPacketToggleSlow;
import de.mennomax.astikoorcarts.packets.SPacketDrawnUpdate;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler
{
    private static int id = 0;
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(AstikoorCarts.MODID);

    public static void registerPackets()
    {
        INSTANCE.registerMessage(CPacketActionKey.ActionKeyPacketHandler.class, CPacketActionKey.class, id++, Side.SERVER);
        INSTANCE.registerMessage(CPacketOpenCartGui.OpenCartGuiPacketHandler.class, CPacketOpenCartGui.class, id++, Side.SERVER);
        INSTANCE.registerMessage(CPacketToggleSlow.ToggleSlowHandler.class, CPacketToggleSlow.class, id++, Side.SERVER);

        INSTANCE.registerMessage(SPacketDrawnUpdate.DrawnUpdatePacketHandler.class, SPacketDrawnUpdate.class, id++, Side.CLIENT);
    }
}
