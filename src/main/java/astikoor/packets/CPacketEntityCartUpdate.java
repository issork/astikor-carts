package astikoor.packets;

import astikoor.entity.EntityCart;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CPacketEntityCartUpdate implements IMessage
{
    private int pullingId;
    private int cartId;

    public CPacketEntityCartUpdate()
    {

    }

    public CPacketEntityCartUpdate(int horseIn, int cartIn)
    {
        pullingId = horseIn;
        cartId = cartIn;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        pullingId = buf.readInt();
        cartId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(pullingId);
        buf.writeInt(cartId);
    }

    public static class EntityCartUpdatePacketHandler implements IMessageHandler<CPacketEntityCartUpdate, IMessage>
    {
        @Override
        public IMessage onMessage(CPacketEntityCartUpdate message, MessageContext ctx)
        {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                EntityCart cart = (EntityCart) Minecraft.getMinecraft().world.getEntityByID(message.cartId);
                Entity pulling = Minecraft.getMinecraft().world.getEntityByID(message.pullingId);
                if(cart.getPulling() == pulling)
                {
                    ((EntityCart) cart).setPulling(null);;
                }
                else
                {
                    cart.setPulling(pulling);
                }
            });
            return null;
        }
    }
}
