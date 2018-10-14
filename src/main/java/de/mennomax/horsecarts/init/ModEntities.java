package de.mennomax.horsecarts.init;

import de.mennomax.horsecarts.AstikoorCarts;
import de.mennomax.horsecarts.client.render.RenderCargoCart;
import de.mennomax.horsecarts.client.render.RenderCarriage;
import de.mennomax.horsecarts.client.render.RenderChariot;
import de.mennomax.horsecarts.client.render.RenderPlowCart;
import de.mennomax.horsecarts.entity.EntityCargoCart;
import de.mennomax.horsecarts.entity.EntityCarriage;
import de.mennomax.horsecarts.entity.EntityChariot;
import de.mennomax.horsecarts.entity.EntityPlowCart;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(AstikoorCarts.MODID)
public class ModEntities
{
    @EventBusSubscriber
    public static class EntityRegistrationHandler
    {
        private static int id = 0;

        @SubscribeEvent
        public static void registerEntities(final RegistryEvent.Register<EntityEntry> event)
        {
            final EntityEntry[] entries = {
                    createEntry(EntityCargoCart.class, "cargocart", 80, 3, false),
                    createEntry(EntityPlowCart.class, "plowcart", 80, 3, false),
                    createEntry(EntityChariot.class, "chariot", 80, 3, false),
                    createEntry(EntityCarriage.class, "carriage", 80, 3, false)
            };

            event.getRegistry().registerAll(entries);
        }

        private static EntityEntry createEntry(final Class<? extends Entity> entityClass, final String name, int trackingRange, int updateFrequency, boolean sendVelocityUpdates)
        {
            final ResourceLocation resourceLocation = new ResourceLocation(AstikoorCarts.MODID, name);
            return EntityEntryBuilder.create().entity(entityClass).id(resourceLocation, id++).name(resourceLocation.toString()).tracker(trackingRange, updateFrequency, sendVelocityUpdates).build();
        }
    }

    public static void registerRenders()
    {
        RenderingRegistry.registerEntityRenderingHandler(EntityCargoCart.class, RenderCargoCart::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityPlowCart.class, RenderPlowCart::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityChariot.class, RenderChariot::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityCarriage.class, RenderCarriage::new);
    }
}
