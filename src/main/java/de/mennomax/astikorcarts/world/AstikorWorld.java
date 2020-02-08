package de.mennomax.astikorcarts.world;

import de.mennomax.astikorcarts.entity.AbstractDrawnEntity;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;

import java.util.Optional;

public interface AstikorWorld {
    final class Capability {
        @CapabilityInject(AstikorWorld.class)
        private static net.minecraftforge.common.capabilities.Capability<AstikorWorld> INSTANCE;
    }

    void addPulling(final AbstractDrawnEntity drawn);

    Optional<AbstractDrawnEntity> getDrawn(final Entity e);

    boolean isPulling(final Entity e);

    void tick();

    static LazyOptional<AstikorWorld> get(final World world) {
        return world.getCapability(AstikorWorld.capability());
    }

    static net.minecraftforge.common.capabilities.Capability<AstikorWorld> capability() {
        return Capability.INSTANCE;
    }
}
