package de.mennomax.astikorcarts.util;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class DefRegister {
    private final String namespace;

    public DefRegister(final String namespace) {
        this.namespace = namespace;
    }

    public <T extends IForgeRegistryEntry<T>> Forge<T> of(final IForgeRegistry<T> registry) {
        return new Forge<>(this.namespace, registry);
    }

    public <T, X extends IForgeRegistryEntry<X>> Vanilla<T, Void> of(final Class<X> type, final Registry<T> registry) {
        return new Vanilla<>(this.namespace, type, registry, (t, v) -> {}, rl -> null);
    }

    public <T, N, X extends IForgeRegistryEntry<X>> Vanilla<T, N> of(final Class<X> type, final Registry<T> registry, final BiConsumer<T, N> callback, final Function<ResourceLocation, N> defaultData) {
        return new Vanilla<>(this.namespace, type, registry, callback, defaultData);
    }

    public void registerAll(final IEventBus bus, final Register... registers) {
        for (final Register register : registers) {
            register.register(bus);
        }
    }

    public interface Register {
        void register(final IEventBus bus);
    }

    public static final class Vanilla<T, N> implements Register {
        final String namespace;
        final Class<? extends IForgeRegistryEntry<?>> type;
        final Registry<T> registry;
        final BiConsumer<T, N> callback;
        final Function<ResourceLocation, N> defaultData;
        final List<Runnable> entries = new ArrayList<>();

        private Vanilla(final String namespace, final Class<? extends IForgeRegistryEntry<?>> type, final Registry<T> registry, final BiConsumer<T, N> callback, final Function<ResourceLocation, N> defaultData) {
            this.namespace = namespace;
            this.type = type;
            this.registry = registry;
            this.callback = callback;
            this.defaultData = defaultData;
        }

        public <U extends T> Supplier<U> make(final String name, final Function<ResourceLocation, U> object) {
            return this.make(name, object, this.defaultData);
        }

        @SuppressWarnings("unchecked")
        public <U extends T> Supplier<U> make(final String name, final Function<ResourceLocation, U> object, final Function<ResourceLocation, N> data) {
            final ResourceLocation key = new ResourceLocation(this.namespace, name);
            this.entries.add(() -> {
                final U u = Registry.register(this.registry, key, object.apply(key));
                this.callback.accept(u, data.apply(key));
            });
            return () -> {
                T t = this.registry.get(key);
                if (t == null) throw new IllegalStateException();
                return (U) t;
            };
        }

        @Override
        public void register(final IEventBus bus) {
            bus.addGenericListener(this.type, (RegistryEvent.Register e) -> this.entries.forEach(Runnable::run));
        }
    }

    public static final class Forge<T extends IForgeRegistryEntry<T>> implements Register {
        final IForgeRegistry<T> registry;
        final String namespace;
        final List<Supplier<? extends T>> entries = new ArrayList<>();

        private Forge(final String namespace, final IForgeRegistry<T> registry) {
            this.namespace = namespace;
            this.registry = registry;
        }

        public <U extends T> RegObject<T, U> make(final String name, final Supplier<U> supplier) {
            return this.make(name, rl -> supplier.get());
        }

        public <U extends T> RegObject<T, U> make(final String name, final Function<ResourceLocation, U> supplier) {
            final ResourceLocation key = new ResourceLocation(this.namespace, name);
            this.entries.add(() -> supplier.apply(key).setRegistryName(key));
            return RegObject.of(key, this.registry);
        }

        @Override
        public void register(final IEventBus bus) {
            bus.addGenericListener(this.registry.getRegistrySuperType(), this::onRegister);
        }

        private void onRegister(final RegistryEvent.Register<T> event) {
            this.entries.forEach(sup -> this.registry.register(sup.get()));
        }
    }
}
