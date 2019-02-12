package de.mennomax.astikoorcarts.packets;

import de.mennomax.astikoorcarts.AstikoorCarts;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CPacketOpenCartGui implements IMessage
{
    private int invId;
    private int cartId;

    public CPacketOpenCartGui()
    {

    }

    public CPacketOpenCartGui(int invIdIn, int cartIdIn)
    {
        invId = invIdIn;
        cartId = cartIdIn;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        invId = buf.readInt();
        cartId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(invId);
        buf.writeInt(cartId);
    }

    public static class OpenCartGuiPacketHandler implements IMessageHandler<CPacketOpenCartGui, IMessage>
    {

        @Override
        public IMessage onMessage(CPacketOpenCartGui message, MessageContext ctx)
        {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.openGui(AstikoorCarts.instance, message.invId, player.world, message.cartId, 0, 0);
            return null;
        }
    }

}
