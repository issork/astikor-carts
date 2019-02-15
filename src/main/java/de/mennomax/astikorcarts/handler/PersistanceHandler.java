package de.mennomax.astikorcarts.handler;

import de.mennomax.astikorcarts.AstikorCarts;
import de.mennomax.astikorcarts.capabilities.IPull;
import de.mennomax.astikorcarts.capabilities.PullProvider;
import de.mennomax.astikorcarts.entity.AbstractDrawn;
import de.mennomax.astikorcarts.entity.ai.EntityAIPullCart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PersistanceHandler
{
    @SubscribeEvent
    public void onAttachCapability(AttachCapabilitiesEvent<Entity> event)
    {
        // null check because of a compability issue with MrCrayfish's Furniture Mod and probably others
        if (event.getObject().world != null && !event.getObject().world.isRemote)
        {
            event.addCapability(new ResourceLocation(AstikorCarts.MODID), new PullProvider());
        }
    }

    @SubscribeEvent
    public void onChunkWatch(ChunkWatchEvent.Watch event)
    {
        for (ClassInheritanceMultiMap<Entity> entitylist : event.getChunkInstance().getEntityLists())
        {
            for (Entity entity : entitylist)
            {
                IPull pullCapability = entity.getCapability(PullProvider.PULL, null);
                if (pullCapability != null && pullCapability.getFirstDrawnUUID() != null)
                {
                    ((AbstractDrawn) ((WorldServer) entity.world).getEntityFromUuid(pullCapability.getFirstDrawnUUID())).setPulling(entity);
                }
            }
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event)
    {
        if (!event.getWorld().isRemote && event.getEntity() instanceof EntityLiving)
        {
            ((EntityLiving) event.getEntity()).tasks.addTask(2, new EntityAIPullCart((EntityLiving) event.getEntity()));
        }
    }
}