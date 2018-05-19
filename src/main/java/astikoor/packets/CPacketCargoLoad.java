package astikoor.packets;

import astikoor.entity.EntityCargoCart;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CPacketCargoLoad implements IMessage
{
    private int load;
    private int cartId;

    public CPacketCargoLoad()
    {

    }

    public CPacketCargoLoad(int loadIn, int cartIn)
    {
        load = loadIn;
        cartId = cartIn;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        load = buf.readInt();
        cartId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(load);
        buf.writeInt(cartId);
    }

    public static class CargoLoadPacketHandler implements IMessageHandler<CPacketCargoLoad, IMessage>
    {
        @Override
        public IMessage onMessage(CPacketCargoLoad message, MessageContext ctx)
        {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                EntityCargoCart cart = (EntityCargoCart) Minecraft.getMinecraft().world.getEntityByID(message.cartId);
                cart.setLoad(message.load);
            });
            return null;
        }
    }
}
