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
        ResourceLocation registryName = new ResourceLocation(AstikoorCarts.MODID, entityName);
        EntityRegistry.registerModEntity(registryName, entityClass, entityName, id++, AstikoorCarts.instance, trackingRange, updateFrequency, sendsVelocityUpdates);
    }

    public static void registerRenders()
    {
        RenderingRegistry.registerEntityRenderingHandler(EntityCargoCart.class, RenderCargoCart::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityPlowCart.class, RenderPlowCart::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityChariot.class, RenderChariot::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityCarriage.class, RenderCarriage::new);
    }
}
