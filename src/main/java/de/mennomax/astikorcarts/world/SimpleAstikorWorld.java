package de.mennomax.astikorcarts.world;

import de.mennomax.astikorcarts.entity.AbstractDrawnEntity;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.entity.Entity;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Optional;

public class SimpleAstikorWorld implements AstikorWorld {
    private final Int2ObjectMap<AbstractDrawnEntity> pulling = new Int2ObjectOpenHashMap<>();

    @Override
    public void addPulling(final AbstractDrawnEntity drawn) {
        @Nullable final Entity pulling = drawn.getPulling();
        if (pulling != null && !(pulling instanceof AbstractDrawnEntity)) {
            this.pulling.put(pulling.getEntityId(), drawn);
        }
    }

    @Nullable
    @Override
    public Optional<AbstractDrawnEntity> getDrawn(final Entity e) {
        return Optional.ofNullable(this.pulling.get(e.getEntityId()));
    }

    @Override
    public boolean isPulling(final Entity e) {
        return this.pulling.containsKey(e.getEntityId());
    }

    @Override
    public void tick() {
        final Iterator<AbstractDrawnEntity> it = this.pulling.values().iterator();
        while (it.hasNext()) {
            final AbstractDrawnEntity cart = it.next();
            if (cart.shouldStopPulledTick()) {
                it.remove();
            } else {
                cart.pulledTick();
            }
        }
    }
}
