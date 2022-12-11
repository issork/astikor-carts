package de.mennomax.astikorcarts.world;

import de.mennomax.astikorcarts.entity.AbstractDrawnEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.stream.Stream;

public interface AstikorWorld {
    final class Capability {
        private Capability() {}

        private static net.minecraftforge.common.capabilities.Capability<AstikorWorld> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});;
    }

    void addPulling(final AbstractDrawnEntity drawn);

    Optional<AbstractDrawnEntity> getDrawn(final Entity e);

    boolean isPulling(final Entity e);

    void tick();

    static LazyOptional<AstikorWorld> get(final Level world) {
        return world.getCapability(Capability.INSTANCE);
    }

    static Stream<AstikorWorld> stream(final Level world) {
        return world.getCapability(Capability.INSTANCE).map(Stream::of).orElse(Stream.empty());
    }

    static ICapabilityProvider createProvider(final NonNullSupplier<AstikorWorld> factory) {
        return new ICapabilityProvider() {
            final LazyOptional<AstikorWorld> instance = LazyOptional.of(factory);

            @Override
            public <T> LazyOptional<T> getCapability(final net.minecraftforge.common.capabilities.Capability<T> cap, @Nullable final Direction side) {
                return Capability.INSTANCE.orEmpty(cap, this.instance);
            }
        };
    }
}
