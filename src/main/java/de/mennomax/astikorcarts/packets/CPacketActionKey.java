package de.mennomax.astikorcarts.packets;

import java.util.List;

import de.mennomax.astikorcarts.capabilities.PullProvider;
import de.mennomax.astikorcarts.entity.AbstractDrawn;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CPacketActionKey implements IMessage
{
    public CPacketActionKey()
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

    public static class ActionKeyPacketHandler implements IMessageHandler<CPacketActionKey, IMessage>
    {

        @Override
        public IMessage onMessage(CPacketActionKey message, MessageContext ctx)
        {
            EntityPlayerMP sender = ctx.getServerHandler().player;
            sender.getServerWorld().addScheduledTask(() -> {
                List<AbstractDrawn> result = sender.getServerWorld().getEntitiesWithinAABB(AbstractDrawn.class, sender.getEntityBoundingBox().grow(3), entity -> entity != sender.getRidingEntity() && entity.isEntityAlive());
                if (!result.isEmpty())
                {
                    Entity target = sender.isRiding() ? sender.getRidingEntity() : sender;
                    AbstractDrawn closest = result.get(0);
                    for (AbstractDrawn cart : result)
                    {
                        if (cart.getPulling() == target)
                        {
                            cart.setPulling(null);
                            return;
                        }
                        if (new Vec3d(cart.posX - sender.posX, cart.posY - sender.posY, cart.posZ - sender.posZ).lengthVector() < new Vec3d(closest.posX - sender.posX, closest.posY - sender.posY, closest.posZ - sender.posZ).lengthVector())
                        {
                            closest = cart;
                        }
                    }
                    if (closest.canBePulledBy(target))
                    {
                        if(target.hasCapability(PullProvider.PULL, null))
                        {
                            AbstractDrawn drawn = target.getCapability(PullProvider.PULL, null).getDrawn();
                            if(drawn != null && drawn.getPulling() == target)
                            {
                                return;
                            }
                        }
                        closest.setPulling(target);
                    }
                }
            });
            return null;
        }
    }
}
