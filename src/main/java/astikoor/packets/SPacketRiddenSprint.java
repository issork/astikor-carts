package astikoor.packets;

import astikoor.entity.EntityRiddenCart;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SPacketRiddenSprint implements IMessage
{

    public SPacketRiddenSprint()
    {

    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        
    }

    public static class RiddenSprintPacketHandler implements IMessageHandler<SPacketRiddenSprint, IMessage>
    {

        @Override
        public IMessage onMessage(SPacketRiddenSprint message, MessageContext ctx)
        {
            final EntityPlayerMP sender = ctx.getServerHandler().player;
            sender.getServerWorld().addScheduledTask(() -> {
                    if(sender.isRiding())
                    {
                        if(sender.getRidingEntity() != null)
                        {
                            if(sender.getRidingEntity() instanceof EntityRiddenCart)
                            {
                                ((EntityRiddenCart) sender.getRidingEntity()).getPulling().setSprinting(true);
                            }
                        }
                    }
            });
            return null;
        }
    }

}
