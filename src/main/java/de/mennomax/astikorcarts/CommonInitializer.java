package de.mennomax.astikorcarts;

import com.google.common.collect.ImmutableMap;
import de.mennomax.astikorcarts.config.AstikorCartsConfig;
import de.mennomax.astikorcarts.entity.PostilionEntity;
import de.mennomax.astikorcarts.entity.ai.goal.PullCartGoal;
import de.mennomax.astikorcarts.entity.ai.goal.RideCartGoal;
import de.mennomax.astikorcarts.util.GoalAdder;
import de.mennomax.astikorcarts.util.RegObject;
import de.mennomax.astikorcarts.world.AstikorWorld;
import de.mennomax.astikorcarts.world.SimpleAstikorWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.item.Item;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Consumer;

public class CommonInitializer implements Initializer {
    @Override
    public void init(final Context mod) {
        mod.context().registerConfig(ModConfig.Type.COMMON, AstikorCartsConfig.COMMON_SPEC);
        mod.modBus().<FMLCommonSetupEvent>addListener(e -> {
            CapabilityManager.INSTANCE.register(AstikorWorld.class, new Capability.IStorage<AstikorWorld>() {
                @Nullable
                @Override
                public INBT writeNBT(final Capability<AstikorWorld> capability, final AstikorWorld instance, final Direction side) {
                    return null;
                }

                @Override
                public void readNBT(final Capability<AstikorWorld> capability, final AstikorWorld instance, final Direction side, final INBT nbt) {
                }
            }, SimpleAstikorWorld::new);
            e.enqueueWork(() -> {
                GlobalEntityTypeAttributes.put(AstikorCarts.EntityTypes.POSTILION.get(), LivingEntity.registerAttributes().create());
            });
        });
        mod.bus().<AttachCapabilitiesEvent<World>, World>addGenericListener(World.class, e ->
            e.addCapability(new ResourceLocation(AstikorCarts.ID, "astikor"), AstikorWorld.createProvider(SimpleAstikorWorld::new))
        );
        mod.bus().register(GoalAdder.mobGoal(MobEntity.class)
            .add(1, PullCartGoal::new)
            .add(1, RideCartGoal::new)
            .build()
        );
        mod.bus().<PlayerInteractEvent.EntityInteract>addListener(e -> {
            final Entity rider = e.getTarget().getControllingPassenger();
            if (rider instanceof PostilionEntity) {
                rider.stopRiding();
            }
        });
        mod.bus().<TickEvent.WorldTickEvent>addListener(e -> {
            if (e.phase == TickEvent.Phase.END) {
                AstikorWorld.get(e.world).ifPresent(AstikorWorld::tick);
            }
        });
        mod.bus().addGenericListener(Item.class, this.remap(ImmutableMap.<String, RegObject<Item, ? extends Item>>builder()
            .put("cargocart", AstikorCarts.Items.CARGO_CART)
            .put("plowcart", AstikorCarts.Items.PLOW_CART)
            .put("mobcart", AstikorCarts.Items.MOB_CART)
            .build()
        ));
        mod.bus().addGenericListener(EntityType.class, this.remap(ImmutableMap.<String, RegObject<EntityType<?>, ? extends EntityType<?>>>builder()
            .put("cargocart", AstikorCarts.EntityTypes.CARGO_CART)
            .put("plowcart", AstikorCarts.EntityTypes.PLOW_CART)
            .put("mobcart", AstikorCarts.EntityTypes.MOB_CART)
            .build()
        ));
    }

    private <T extends IForgeRegistryEntry<T>> Consumer<RegistryEvent.MissingMappings<T>> remap(final Map<String, RegObject<T, ? extends T>> objects) {
        return e -> {
            for (final RegistryEvent.MissingMappings.Mapping<T> mapping : e.getAllMappings()) {
                if (AstikorCarts.ID.equals(mapping.key.getNamespace())) {
                    final RegObject<T, ? extends T> target = objects.get(mapping.key.getPath());
                    if (target != null) {
                        mapping.remap(target.get());
                    }
                }
            }
        };
    }
}
