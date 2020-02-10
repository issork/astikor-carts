package de.mennomax.astikorcarts;

import de.mennomax.astikorcarts.client.ClientInitializer;
import de.mennomax.astikorcarts.item.CartItem;
import de.mennomax.astikorcarts.server.ServerInitializer;
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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Mod(AstikorCarts.ID)
public final class AstikorCarts {
    public static final String ID = "astikorcarts";

    static class ModRegister {
        final String namespace;
        final List<DefRegister<?>> registrars = new ArrayList<>();

        ModRegister(final String namespace) {
            this.namespace = namespace;
        }

        <T extends IForgeRegistryEntry<T>> DefRegister<T> type(final IForgeRegistry<T> registry) {
            final DefRegister<T> reg = new DefRegister<>(registry, this.namespace);
            this.registrars.add(reg);
            return reg;
        }

        void register(final IEventBus bus) {
            this.registrars.forEach(r -> bus.addListener(r::onRegister));
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
            if (event.getRegistry() == this.registry) {
                this.entries.forEach(sup -> this.registry.register(sup.get()));
            }
        }
    }

    private static final ModRegister OBJECTS = new ModRegister(ID);

    public static final class Items {
        private Items() {}

        public static final RegObject<Item> WHEEL, CARGO_CART, PLOW_CART, MOB_CART;

        static {
            final DefRegister<Item> r = OBJECTS.type(ForgeRegistries.ITEMS);
            WHEEL = r.make("wheel", () -> new Item(new Item.Properties().group(ItemGroup.MATERIALS)));
            final Supplier<Item> cart = () -> new CartItem(new Item.Properties().maxStackSize(1).group(ItemGroup.TRANSPORTATION));
            CARGO_CART = r.make("cargocart", cart);
            PLOW_CART = r.make("plowcart", cart);
            MOB_CART = r.make("mobcart", cart);
        }
    }

    public static final class EntityTypes {}

    public static final class SoundEvents {}

    public static final class Stats {}

    public static final class ContainerTypes {}

    public AstikorCarts() {
        final Initializer.Context mod = new InitContext();
        //OBJECTS.register(mod.modBus());
        DistExecutor.runForDist(() -> ClientInitializer::new, () -> ServerInitializer::new).init(mod);
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
