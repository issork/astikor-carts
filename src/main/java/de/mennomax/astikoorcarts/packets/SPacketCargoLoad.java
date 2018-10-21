package de.mennomax.astikoorcarts.packets;

import de.mennomax.astikoorcarts.entity.EntityCargoCart;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SPacketCargoLoad implements IMessage
{
    private int load;
    private int cartId;

    public SPacketCargoLoad()
    {

    }

    public SPacketCargoLoad(int loadIn, int cartIn)
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

    public static class CargoLoadPacketHandler implements IMessageHandler<SPacketCargoLoad, IMessage>
    {
        @Override
        public IMessage onMessage(SPacketCargoLoad message, MessageContext ctx)
        {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                EntityCargoCart cart = (EntityCargoCart) Minecraft.getMinecraft().world.getEntityByID(message.cartId);
                cart.setLoad(message.load);
            });
            return null;
        }
    }
}
