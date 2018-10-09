package de.mennomax.horsecarts.packets;

import de.mennomax.horsecarts.entity.EntityRiddenCart;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CPacketRiddenSprint implements IMessage
{

    public CPacketRiddenSprint()
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

    public static class RiddenSprintPacketHandler implements IMessageHandler<CPacketRiddenSprint, IMessage>
    {

        @Override
        public IMessage onMessage(CPacketRiddenSprint message, MessageContext ctx)
        {
            final EntityPlayerMP sender = ctx.getServerHandler().player;
            sender.getServerWorld().addScheduledTask(() -> {
                if(sender.isRiding())
                {
                    if(sender.getRidingEntity() instanceof EntityRiddenCart)
                    {
                        ((EntityRiddenCart) sender.getRidingEntity()).getPulling().setSprinting(true);
                    }
                }
            });
            return null;
        }
    }

}
