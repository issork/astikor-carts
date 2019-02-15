package de.mennomax.astikorcarts.handler;

import de.mennomax.astikorcarts.AstikorCarts;
import de.mennomax.astikorcarts.packets.CPacketActionKey;
import de.mennomax.astikorcarts.packets.CPacketOpenCartGui;
import de.mennomax.astikorcarts.packets.CPacketToggleSlow;
import de.mennomax.astikorcarts.packets.SPacketDrawnUpdate;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler
{
    private static int id = 0;
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(AstikorCarts.MODID);

    public static void registerPackets()
    {
        INSTANCE.registerMessage(CPacketActionKey.ActionKeyPacketHandler.class, CPacketActionKey.class, id++, Side.SERVER);
        INSTANCE.registerMessage(CPacketOpenCartGui.OpenCartGuiPacketHandler.class, CPacketOpenCartGui.class, id++, Side.SERVER);
        INSTANCE.registerMessage(CPacketToggleSlow.ToggleSlowHandler.class, CPacketToggleSlow.class, id++, Side.SERVER);

        INSTANCE.registerMessage(SPacketDrawnUpdate.DrawnUpdatePacketHandler.class, SPacketDrawnUpdate.class, id++, Side.CLIENT);
    }
}
