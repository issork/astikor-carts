package de.mennomax.astikoorcarts.packets;

import de.mennomax.astikoorcarts.entity.AbstractRiddenDrawn;
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
            EntityPlayerMP sender = ctx.getServerHandler().player;
            sender.getServerWorld().addScheduledTask(() -> {
                if (sender.isRiding())
                {
                    if (sender.getRidingEntity() instanceof AbstractRiddenDrawn)
                    {
                        ((AbstractRiddenDrawn) sender.getRidingEntity()).getPulling().setSprinting(true);
                    }
                }
            });
            return null;
        }
    }

}
