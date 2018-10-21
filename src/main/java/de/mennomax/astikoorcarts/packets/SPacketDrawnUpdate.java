package de.mennomax.astikoorcarts.packets;

import de.mennomax.astikoorcarts.entity.AbstractDrawn;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SPacketDrawnUpdate implements IMessage
{
    private int pullingId;
    private int cartId;

    public SPacketDrawnUpdate()
    {

    }

    public SPacketDrawnUpdate(int horseIn, int cartIn)
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

    public static class DrawnUpdatePacketHandler implements IMessageHandler<SPacketDrawnUpdate, IMessage>
    {
        @Override
        public IMessage onMessage(SPacketDrawnUpdate message, MessageContext ctx)
        {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                AbstractDrawn cart = (AbstractDrawn) Minecraft.getMinecraft().world.getEntityByID(message.cartId);
                if (message.pullingId < 0)
                {
                    cart.setPulling(null);
                    return;
                }
                else
                {
                    cart.setPulling(Minecraft.getMinecraft().world.getEntityByID(message.pullingId));
                }
            });
            return null;
        }
    }
}
