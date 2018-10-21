package de.mennomax.astikoorcarts.handler;

import de.mennomax.astikoorcarts.AstikoorCarts;
import de.mennomax.astikoorcarts.capabilities.PullProvider;
import de.mennomax.astikoorcarts.entity.AbstractDrawn;
import de.mennomax.astikoorcarts.entity.ai.EntityAIPullCart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CapabilityEventHandler
{
    @SubscribeEvent
    public void onAttachCapability(AttachCapabilitiesEvent<Entity> event)
    {
        if (!event.getObject().world.isRemote)
        {
//            if (event.getObject() instanceof EntityLiving)
//            {
            event.addCapability(new ResourceLocation(AstikoorCarts.MODID), new PullProvider());
//            }  
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event)
    {
        if (event.getEntity() instanceof EntityLiving)
        {
            ((EntityLiving) event.getEntity()).tasks.addTask(2, new EntityAIPullCart((EntityLiving) event.getEntity()));
        }
        if (!event.getWorld().isRemote)
        {
            AbstractDrawn drawn = (AbstractDrawn) ((WorldServer) event.getWorld()).getEntityFromUuid(event.getEntity().getCapability(PullProvider.PULL, null).getFirstDrawnUUID());
            if (drawn != null)
            {
                drawn.setPulling(event.getEntity());
            }
        }
    }
}
