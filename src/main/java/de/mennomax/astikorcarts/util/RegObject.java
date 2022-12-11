package de.mennomax.astikorcarts.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.ObjectHolderRegistry;

import javax.annotation.Nullable;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class RegObject<T extends IForgeRegistryEntry<T>, U extends T> implements Predicate<T> {
    private final ResourceLocation name;

    @Nullable
    private U value;

    public RegObject(final ResourceLocation name) {
        this.name = name;
    }

    public U get() {
        if (this.value == null) {
            throw new NoSuchElementException("Registry object not present: " + this.name);
        }
        return this.value;
    }

    @Override
    public boolean test(final T other) {
        return this.value != null && this.value == other;
    }

    public Stream<T> stream() {
        return this.value == null ? Stream.empty() : Stream.of(this.value);
    }

    public static <T extends IForgeRegistryEntry<T>, U extends T> RegObject<T, U> of(final ResourceLocation name, final IForgeRegistry<T> registry) {
        final RegObject<T, U> obj = new RegObject<>(name);
        ObjectHolderRegistry.addHandler(n -> {
            if (n.test(registry.getRegistryName())) {
                //noinspection unchecked
                obj.value = registry.containsKey(name) ? (U) registry.getValue(name) : null;
            }
        });
        return obj;
    }
}
