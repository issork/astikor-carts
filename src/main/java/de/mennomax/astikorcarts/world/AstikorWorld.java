package de.mennomax.astikorcarts.world;

import de.mennomax.astikorcarts.entity.AbstractDrawnEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;

import javax.annotation.Nullable;
import java.util.Optional;

public interface AstikorWorld {
    final class Capability {
        private Capability() {}

        @CapabilityInject(AstikorWorld.class)
        private static net.minecraftforge.common.capabilities.Capability<AstikorWorld> INSTANCE;
    }

    void addPulling(final AbstractDrawnEntity drawn);

    Optional<AbstractDrawnEntity> getDrawn(final Entity e);

    boolean isPulling(final Entity e);

    void tick();

    static LazyOptional<AstikorWorld> get(final World world) {
        return world.getCapability(Capability.INSTANCE);
    }

    static ICapabilityProvider createProvider(final NonNullSupplier<AstikorWorld> factory) {
        return new ICapabilityProvider() {
            final LazyOptional<AstikorWorld> instance = LazyOptional.of(factory);

            @Override
            public <T> LazyOptional<T> getCapability(final net.minecraftforge.common.capabilities.Capability<T> cap, @Nullable final Direction side) {
                return cap == Capability.INSTANCE ? this.instance.cast() : LazyOptional.empty();
            }
        };
    }
}
