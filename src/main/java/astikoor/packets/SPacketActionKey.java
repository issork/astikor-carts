package astikoor.packets;

import java.util.List;

import astikoor.config.ModConfig;
import astikoor.entity.EntityCart;
import astikoor.handler.PacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SPacketActionKey implements IMessage
{
    public SPacketActionKey()
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

    public static class ActionKeyPacketHandler implements IMessageHandler<SPacketActionKey, IMessage>
    {

        @Override
        public IMessage onMessage(SPacketActionKey message, MessageContext ctx)
        {
            final EntityPlayerMP sender = ctx.getServerHandler().player;
            sender.getServerWorld().addScheduledTask(() -> {
                if(sender.isRiding())
                {
                    List<EntityCart> result = sender.getServerWorld().getEntitiesWithinAABB(EntityCart.class, sender.getEntityBoundingBox().grow(3), entity -> entity != sender.getRidingEntity() && entity.isEntityAlive());
                    if(!result.isEmpty())
                    {
                        EntityCart closest = result.get(0);
                        for(EntityCart cart : result)
                        {
                            if(cart.getPulling() == sender.getRidingEntity())
                            {
                                sender.getServerWorld().getEntityTracker().sendToTracking(cart, PacketHandler.INSTANCE.getPacketFrom(new CPacketEntityCartUpdate(sender.getRidingEntity().getEntityId(), cart.getEntityId())));
                                cart.setPulling(null);
                                return;
                            }
                            if(new Vec3d(cart.posX-sender.posX, cart.posY-sender.posY, cart.posZ-sender.posZ).lengthVector() < new Vec3d(closest.posX-sender.posX, closest.posY-sender.posY, closest.posZ-sender.posZ).lengthVector())
                            {
                                closest = cart;
                            }
                        }
                        if(closest.canPull(sender.getRidingEntity()))
                        {
                            sender.getServerWorld().getEntityTracker().sendToTracking(closest, PacketHandler.INSTANCE.getPacketFrom(new CPacketEntityCartUpdate(sender.getRidingEntity().getEntityId(), closest.getEntityId())));
                            closest.setPulling(sender.getRidingEntity());
                        }
                    }
                }
            });
            return null;
        }
    }
}
