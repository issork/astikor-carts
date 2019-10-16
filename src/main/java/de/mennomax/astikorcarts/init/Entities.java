package de.mennomax.astikorcarts.init;

import de.mennomax.astikorcarts.AstikorCarts;
import de.mennomax.astikorcarts.client.renderer.entity.CargoCartRenderer;
import de.mennomax.astikorcarts.client.renderer.entity.MobCartRenderer;
import de.mennomax.astikorcarts.client.renderer.entity.PlowCartRenderer;
import de.mennomax.astikorcarts.entity.CargoCartEntity;
import de.mennomax.astikorcarts.entity.MobCartEntity;
import de.mennomax.astikorcarts.entity.PlowCartEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntityType.IFactory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(AstikorCarts.MODID)
@EventBusSubscriber(bus = Bus.MOD, modid = AstikorCarts.MODID)
public class Entities {

    public static final EntityType<?> CARGOCART = null;
    public static final EntityType<?> PLOWCART = null;
    public static final EntityType<?> MOBCART = null;

    @SubscribeEvent
    public static void registerEntities(final RegistryEvent.Register<EntityType<?>> event) {
        event.getRegistry().registerAll(buildType(CargoCartEntity::new, "cargocart", 1.5F, 1.4F),
                buildType(PlowCartEntity::new, "plowcart", 1.3F, 1.4F),
                buildType(MobCartEntity::new, "mobcart", 1.3F, 1.4F));
    }

    public static EntityType<?> buildType(final IFactory<Entity> factoryIn, final String name, final float widthIn, final float heightIn) {
        final ResourceLocation resourceLocation = new ResourceLocation(AstikorCarts.MODID, name);
        final EntityType<?> entityType = EntityType.Builder.create(factoryIn, EntityClassification.MISC)
                .size(widthIn, heightIn)
                .build(resourceLocation.toString());
        entityType.setRegistryName(name);
        return entityType;
    }

    @SubscribeEvent
    public static void registerRenders(final FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(CargoCartEntity.class, CargoCartRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(PlowCartEntity.class, PlowCartRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(MobCartEntity.class, MobCartRenderer::new);
    }
}
