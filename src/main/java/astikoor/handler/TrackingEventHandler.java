package astikoor.handler;

import astikoor.entity.EntityCargoCart;
import astikoor.entity.EntityCart;
import astikoor.packets.CPacketCargoLoad;
import astikoor.packets.CPacketEntityCartUpdate;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TrackingEventHandler
{
    @SubscribeEvent
    public void onStartTracking(PlayerEvent.StartTracking event)
    {
        if(event.getTarget() instanceof EntityCart)
        {
            EntityCart target = (EntityCart) event.getTarget();
            if(target.getPulling() != null)
            {
                PacketHandler.INSTANCE.sendTo(new CPacketEntityCartUpdate(target.getPulling().getEntityId(), target.getEntityId()), (EntityPlayerMP) event.getEntityPlayer());
            }
        }
        if(event.getTarget() instanceof EntityCargoCart)
        {
            EntityCargoCart target = (EntityCargoCart) event.getTarget();
            PacketHandler.INSTANCE.sendTo(new CPacketCargoLoad(target.getLoad(), target.getEntityId()), (EntityPlayerMP) event.getEntityPlayer());
        }
    }
}
