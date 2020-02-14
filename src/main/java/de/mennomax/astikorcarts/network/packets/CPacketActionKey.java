package de.mennomax.astikorcarts.network.packets;

import com.mojang.datafixers.util.Pair;
import de.mennomax.astikorcarts.entity.AbstractDrawnEntity;
import de.mennomax.astikorcarts.world.AstikorWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public final class CPacketActionKey {
    public static void encode(final CPacketActionKey packet, final PacketBuffer buffer) {

    }

    public static CPacketActionKey decode(final PacketBuffer buffer) {
        return new CPacketActionKey();
    }

    public static void handle(final CPacketActionKey msg, final Supplier<Context> ctx) {
        final ServerPlayerEntity player = ctx.get().getSender();
        if (player != null) {
            ctx.get().enqueueWork(() -> {
                final Entity pulling = player.isPassenger() ? Objects.requireNonNull(player.getRidingEntity()) : player;
                final World world = player.world;
                AstikorWorld.get(world).map(w -> w.getDrawn(pulling)).orElse(Optional.empty())
                    .map(c -> Optional.of(Pair.of(c, (Entity) null)))
                    .orElseGet(() -> world.getEntitiesWithinAABB(AbstractDrawnEntity.class, player.getBoundingBox().grow(3), entity -> entity != pulling).stream()
                        .min(Comparator.comparing(pulling::getDistance))
                        .map(c -> Pair.of(c, pulling))
                    ).ifPresent(p -> p.getFirst().setPulling(p.getSecond()));
            });
        }
        ctx.get().setPacketHandled(true);
    }
}
