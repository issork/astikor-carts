package de.mennomax.astikorcarts.network.packets;

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

public final class CPacketActionKey implements Message {
    @Override
    public void encode(final PacketBuffer buf) {
    }

    @Override
    public void decode(final PacketBuffer buf) {
    }

    public static void handle(final CPacketActionKey msg, final ServerMessageContext ctx) {
        final ServerPlayerEntity player = ctx.getPlayer();
        final Entity pulling = player.isPassenger() ? Objects.requireNonNull(player.getRidingEntity()) : player;
        final World world = player.world;
        AstikorWorld.get(world).map(w -> w.getDrawn(pulling)).orElse(Optional.empty())
            .map(c -> Optional.of(Pair.of(c, (Entity) null)))
            .orElseGet(() -> world.getEntitiesWithinAABB(AbstractDrawnEntity.class, player.getBoundingBox().grow(3), entity -> entity != pulling).stream()
                .min(Comparator.comparing(pulling::getDistance))
                .map(c -> Pair.of(c, pulling))
            ).ifPresent(p -> p.getFirst().setPulling(p.getSecond()));
    }
}
