package de.mennomax.horsecarts.handler;

import de.mennomax.horsecarts.packets.CPacketActionKey;
import de.mennomax.horsecarts.packets.CPacketMoveCart;
import de.mennomax.horsecarts.packets.CPacketOpenCartGui;
import de.mennomax.horsecarts.packets.CPacketRiddenSprint;
import de.mennomax.horsecarts.packets.SPacketCargoLoad;
import de.mennomax.horsecarts.packets.SPacketEntityCartUpdate;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler
{
    private static int id = 0;
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("astikoor");

    public static void registerPackets()
    {
        INSTANCE.registerMessage(CPacketActionKey.ActionKeyPacketHandler.class, CPacketActionKey.class, id++, Side.SERVER);
        INSTANCE.registerMessage(CPacketMoveCart.MoveCartPacketHandler.class, CPacketMoveCart.class, id++, Side.SERVER);
        INSTANCE.registerMessage(CPacketRiddenSprint.RiddenSprintPacketHandler.class, CPacketRiddenSprint.class, id++, Side.SERVER);
        INSTANCE.registerMessage(CPacketOpenCartGui.OpenCartGuiPacketHandler.class, CPacketOpenCartGui.class, id++, Side.SERVER);

        INSTANCE.registerMessage(SPacketEntityCartUpdate.EntityCartUpdatePacketHandler.class, SPacketEntityCartUpdate.class, id++, Side.CLIENT);
        INSTANCE.registerMessage(SPacketCargoLoad.CargoLoadPacketHandler.class, SPacketCargoLoad.class, id++, Side.CLIENT);
    }
}
