package de.mennomax.astikorcarts;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.ObjectHolderRegistry;

import javax.annotation.Nullable;
import java.util.NoSuchElementException;

public final class RegObject<T extends IForgeRegistryEntry<? super T>> {
    private final ResourceLocation name;

    @Nullable
    private T value;

    public RegObject(final ResourceLocation name) {
        this.name = name;
    }

    public T get() {
        if (this.value == null) {
            throw new NoSuchElementException("Registry object not present: " + this.name);
        }
        return this.value;
    }

    public static <T extends IForgeRegistryEntry<T>, U extends T> RegObject<U> of(final ResourceLocation name, final IForgeRegistry<T> registry) {
        final RegObject<U> obj = new RegObject<>(name);
        ObjectHolderRegistry.addHandler(n -> {
            if (n.test(registry.getRegistryName())) {
                //noinspection unchecked
                obj.value = registry.containsKey(name) ? (U) registry.getValue(name) : null;
            }
        });
        return obj;
    }
}
