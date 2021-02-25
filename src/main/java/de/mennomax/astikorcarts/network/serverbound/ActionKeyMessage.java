package de.mennomax.astikorcarts.network.serverbound;

import com.google.common.base.MoreObjects;
import com.mojang.datafixers.util.Pair;
import de.mennomax.astikorcarts.entity.AbstractDrawnEntity;
import de.mennomax.astikorcarts.network.Message;
import de.mennomax.astikorcarts.network.ServerMessageContext;
import de.mennomax.astikorcarts.world.AstikorWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

public final class ActionKeyMessage implements Message {
    @Override
    public void encode(final PacketBuffer buf) {
    }

    @Override
    public void decode(final PacketBuffer buf) {
    }

    public static void handle(final ActionKeyMessage msg, final ServerMessageContext ctx) {
        final ServerPlayerEntity player = ctx.getPlayer();
        final Entity pulling = MoreObjects.firstNonNull(player.getRidingEntity(), player);
        final World world = player.world;
        AstikorWorld.get(world).map(w -> w.getDrawn(pulling)).orElse(Optional.empty())
            .map(c -> Optional.of(Pair.of(c, (Entity) null)))
            .orElseGet(() -> world.getEntitiesWithinAABB(AbstractDrawnEntity.class, pulling.getBoundingBox().grow(2.0D), entity -> entity != pulling).stream()
                .min(Comparator.comparing(pulling::getDistance))
                .map(c -> Pair.of(c, pulling))
            ).ifPresent(p -> p.getFirst().setPulling(p.getSecond()));
    }
}
