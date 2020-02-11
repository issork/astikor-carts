package de.mennomax.astikorcarts;

import de.mennomax.astikorcarts.client.ClientInitializer;
import de.mennomax.astikorcarts.entity.CargoCartEntity;
import de.mennomax.astikorcarts.entity.MobCartEntity;
import de.mennomax.astikorcarts.entity.PlowCartEntity;
import de.mennomax.astikorcarts.entity.PostilionEntity;
import de.mennomax.astikorcarts.init.EntityBuilder;
import de.mennomax.astikorcarts.item.CartItem;
import de.mennomax.astikorcarts.server.ServerInitializer;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Mod(AstikorCarts.ID)
public final class AstikorCarts {
    public static final String ID = "astikorcarts";

    static class ModRegister {
        final String namespace;

        ModRegister(final String namespace) {
            this.namespace = namespace;
        }

        <T extends IForgeRegistryEntry<T>> DefRegister<T> type(final IForgeRegistry<T> registry) {
            return new DefRegister<>(registry, this.namespace);
        }

        void registerAll(final IEventBus bus, final DefRegister<?>... registers) {
            for (final DefRegister<?> register : registers) {
                bus.addListener(register::onRegister);
            }
        }
    }

    static class DefRegister<T extends IForgeRegistryEntry<T>> {
        final IForgeRegistry<T> registry;
        final String namespace;
        final List<Supplier<? extends T>> entries = new ArrayList<>();

        DefRegister(final IForgeRegistry<T> registry, final String namespace) {
            this.registry = registry;
            this.namespace = namespace;
        }

        <U extends T> RegObject<U> make(final String name, final Supplier<U> supplier) {
            final ResourceLocation key = new ResourceLocation(this.namespace, name);
            this.entries.add(() -> supplier.get().setRegistryName(key));
            return RegObject.of(key, this.registry);
        }

        void onRegister(final RegistryEvent.Register<?> event) {
            LogManager.getLogger().info("{}", event.getRegistry().getRegistrySuperType());
            if (event.getRegistry() == this.registry) {
                this.entries.forEach(sup -> this.registry.register(sup.get()));
            }
        }
    }

    private static final ModRegister OBJECTS = new ModRegister(ID);

    public static final class Items {
        private Items() {}

        private static final DefRegister<Item> R = OBJECTS.type(ForgeRegistries.ITEMS);

        public static final RegObject<Item> WHEEL, CARGO_CART, PLOW_CART, MOB_CART;

        static {
            WHEEL = R.make("wheel", () -> new Item(new Item.Properties().group(ItemGroup.MATERIALS)));
            final Supplier<Item> cart = () -> new CartItem(new Item.Properties().maxStackSize(1).group(ItemGroup.TRANSPORTATION));
            CARGO_CART = R.make("cargocart", cart);
            PLOW_CART = R.make("plowcart", cart);
            MOB_CART = R.make("mobcart", cart);
        }
    }

    public static final class EntityTypes {
        private EntityTypes() {}

        private static final DefRegister<EntityType<?>> R = OBJECTS.type(ForgeRegistries.ENTITIES);

        public static final RegObject<EntityType<CargoCartEntity>> CARGO_CART;
        public static final RegObject<EntityType<PlowCartEntity>> PLOW_CART;
        public static final RegObject<EntityType<MobCartEntity>> MOB_CART;
        public static final RegObject<EntityType<PostilionEntity>> POSTILION;

        static {
            CARGO_CART = R.make("cargocart", () -> EntityBuilder.create(CargoCartEntity::new, EntityClassification.MISC)
                .size(1.5F, 1.4F)
                .build());
            PLOW_CART = R.make("plowcart", () -> EntityBuilder.create(PlowCartEntity::new, EntityClassification.MISC)
                .size(1.3F, 1.4F)
                .build());
            MOB_CART = R.make("mobcart", () -> EntityBuilder.create(MobCartEntity::new, EntityClassification.MISC)
                .size(1.3F, 1.4F)
                .build());
            POSTILION = R.make("postilion", () -> EntityBuilder.create(PostilionEntity::new, EntityClassification.MISC)
                .size(0.25F, 0.25F)
                .insummonable()
                .unserializable()
                .build());
        }
    }

    public static final class SoundEvents {
        private SoundEvents() {}
    }

    public static final class Stats {}

    public static final class ContainerTypes {}

    public AstikorCarts() {
        final Initializer.Context ctx = new InitContext();
        DistExecutor.runForDist(() -> ClientInitializer::new, () -> ServerInitializer::new).init(ctx);
        OBJECTS.registerAll(ctx.modBus(), Items.R, EntityTypes.R);
    }

    private static class InitContext implements Initializer.Context {
        @Override
        public ModLoadingContext context() {
            return ModLoadingContext.get();
        }

        @Override
        public IEventBus bus() {
            return MinecraftForge.EVENT_BUS;
        }

        @Override
        public IEventBus modBus() {
            return FMLJavaModLoadingContext.get().getModEventBus();
        }
    }
}
