package de.mennomax.horsecarts.handler;

import de.mennomax.horsecarts.entity.EntityCargoCart;
import de.mennomax.horsecarts.entity.EntityCart;
import de.mennomax.horsecarts.packets.SPacketCargoLoad;
import de.mennomax.horsecarts.packets.SPacketEntityCartUpdate;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TrackingEventHandler
{
    @SubscribeEvent
    public void onStartTracking(PlayerEvent.StartTracking event)
    {
        if (event.getTarget() instanceof EntityCart)
        {
            EntityCart target = (EntityCart) event.getTarget();
            if (target.getPulling() != null)
            {
                PacketHandler.INSTANCE.sendTo(new SPacketEntityCartUpdate(target.getPulling().getEntityId(), target.getEntityId()), (EntityPlayerMP) event.getEntityPlayer());
            }
        }
        if (event.getTarget() instanceof EntityCargoCart)
        {
            EntityCargoCart target = (EntityCargoCart) event.getTarget();
            PacketHandler.INSTANCE.sendTo(new SPacketCargoLoad(target.getLoad(), target.getEntityId()), (EntityPlayerMP) event.getEntityPlayer());
        }
    }
}
