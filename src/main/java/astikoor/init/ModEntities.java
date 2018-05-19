package astikoor.init;

import astikoor.Astikoor;
import astikoor.client.render.RenderCargoCart;
import astikoor.client.render.RenderCarriage;
import astikoor.client.render.RenderChariot;
import astikoor.client.render.RenderPlowCart;
import astikoor.entity.EntityCargoCart;
import astikoor.entity.EntityCarriage;
import astikoor.entity.EntityChariot;
import astikoor.entity.EntityPlowCart;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.registry.EntityRegistry;

@EventBusSubscriber
public class ModEntities
{
    private static int id = 0;

    public static void registerEntities()
    {
        registerEntity(EntityCargoCart.class, "cargocart", 80, 3, false);
        registerEntity(EntityPlowCart.class, "plowcart", 80, 3, false);
        registerEntity(EntityChariot.class, "chariot", 80, 3, false);
        registerEntity(EntityCarriage.class, "carriage", 80, 3, false);
    }

    private static void registerEntity(Class<? extends Entity> entityClass, String entityName, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates)
    {
        ResourceLocation registryName = new ResourceLocation(Astikoor.MODID, entityName);
        EntityRegistry.registerModEntity(registryName, entityClass, entityName, id++, Astikoor.instance, trackingRange, updateFrequency, sendsVelocityUpdates);
    }

    public static void registerRenders()
    {
        RenderingRegistry.registerEntityRenderingHandler(EntityCargoCart.class, RenderCargoCart::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityPlowCart.class, RenderPlowCart::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityChariot.class, RenderChariot::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityCarriage.class, RenderCarriage::new);
    }
}
