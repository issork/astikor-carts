package astikoor.packets;

import astikoor.entity.EntityRiddenCart;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SPacketMoveCart implements IMessage
{
    private boolean forward;

    public SPacketMoveCart()
    {

    }

    public SPacketMoveCart(boolean forwardIn)
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

    public static class MoveCartPacketHandler implements IMessageHandler<SPacketMoveCart, IMessage>
    {

        @Override
        public IMessage onMessage(SPacketMoveCart message, MessageContext ctx)
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
