package de.mennomax.astikorcarts.init;

import de.mennomax.astikorcarts.AstikorCarts;
import de.mennomax.astikorcarts.entity.CargoCartEntity;
import de.mennomax.astikorcarts.entity.MobCartEntity;
import de.mennomax.astikorcarts.entity.PlowCartEntity;
import de.mennomax.astikorcarts.entity.PostilionEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntityType.IFactory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(AstikorCarts.ID)
@EventBusSubscriber(bus = Bus.MOD, modid = AstikorCarts.ID)
public class Entities {

    public static final EntityType<?> CARGOCART = null;
    public static final EntityType<?> PLOWCART = null;
    public static final EntityType<?> MOBCART = null;
    public static final EntityType<PostilionEntity> POSTILION = null;

    @SubscribeEvent
    public static void registerEntities(final RegistryEvent.Register<EntityType<?>> event) {
        event.getRegistry().registerAll(buildType(CargoCartEntity::new, "cargocart", 1.5F, 1.4F),
            buildType(PlowCartEntity::new, "plowcart", 1.3F, 1.4F),
            buildType(MobCartEntity::new, "mobcart", 1.3F, 1.4F),
            EntityType.Builder.<PostilionEntity>create(PostilionEntity::new, EntityClassification.MISC)
                .size(0.25F, 0.25F)
                .disableSummoning()
                .disableSerialization()
                .setCustomClientFactory((pkt, world) -> new PostilionEntity(world))
                .build(AstikorCarts.ID + ":postilion")
                .setRegistryName("postilion")
        );
    }

    public static EntityType<?> buildType(final IFactory<Entity> factoryIn, final String name, final float widthIn, final float heightIn) {
        final ResourceLocation resourceLocation = new ResourceLocation(AstikorCarts.ID, name);
        final EntityType<?> entityType = EntityType.Builder.create(factoryIn, EntityClassification.MISC)
            .size(widthIn, heightIn)
            .setCustomClientFactory((pkg, world) -> ForgeRegistries.ENTITIES.getValue(resourceLocation).create(world))
            .build(resourceLocation.toString());
        entityType.setRegistryName(name);
        return entityType;
    }
}
