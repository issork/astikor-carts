package astikoor.handler;

import astikoor.packets.CPacketCargoLoad;
import astikoor.packets.CPacketEntityCartUpdate;
import astikoor.packets.SPacketActionKey;
import astikoor.packets.SPacketMoveCart;
import astikoor.packets.SPacketRiddenSprint;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler
{
    private static int id = 0;
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("astikoor");

    public static void registerPackets()
    {
        INSTANCE.registerMessage(SPacketActionKey.ActionKeyPacketHandler.class, SPacketActionKey.class, id++, Side.SERVER);
        INSTANCE.registerMessage(SPacketMoveCart.MoveCartPacketHandler.class, SPacketMoveCart.class, id++, Side.SERVER);
        INSTANCE.registerMessage(SPacketRiddenSprint.RiddenSprintPacketHandler.class, SPacketRiddenSprint.class, id++, Side.SERVER);
        
        INSTANCE.registerMessage(CPacketEntityCartUpdate.EntityCartUpdatePacketHandler.class, CPacketEntityCartUpdate.class, id++, Side.CLIENT);
        INSTANCE.registerMessage(CPacketCargoLoad.CargoLoadPacketHandler.class, CPacketCargoLoad.class, id++, Side.CLIENT);
    }
}
