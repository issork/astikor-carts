package de.mennomax.horsecarts.packets;

import de.mennomax.horsecarts.entity.EntityRiddenCart;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CPacketMoveCart implements IMessage
{
    private boolean forward;

    public CPacketMoveCart()
    {

    }

    public CPacketMoveCart(boolean forwardIn)
    {
        forward = forwardIn;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        forward = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeBoolean(forward);
    }

    public static class MoveCartPacketHandler implements IMessageHandler<CPacketMoveCart, IMessage>
    {

        @Override
        public IMessage onMessage(CPacketMoveCart message, MessageContext ctx)
        {
            final EntityPlayerMP sender = ctx.getServerHandler().player;
            sender.getServerWorld().addScheduledTask(() -> {
                if(sender.isRiding())
                {
                    if(sender.getRidingEntity() instanceof EntityRiddenCart)
                    {
                        ((EntityRiddenCart) sender.getRidingEntity()).updateForward(message.forward);
                    }
                }
            });
            return null;
        }
    }

}
