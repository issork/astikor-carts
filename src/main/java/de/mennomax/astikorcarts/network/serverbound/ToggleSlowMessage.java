package de.mennomax.astikorcarts.network.serverbound;

import de.mennomax.astikorcarts.entity.AbstractDrawnEntity;
import de.mennomax.astikorcarts.network.Message;
import de.mennomax.astikorcarts.network.ServerMessageContext;
import de.mennomax.astikorcarts.world.AstikorWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;

import java.util.Optional;

public final class ToggleSlowMessage implements Message {
    @Override
    public void encode(final PacketBuffer buf) {
    }

    @Override
    public void decode(final PacketBuffer buf) {
    }

    public static void handle(final ToggleSlowMessage msg, final ServerMessageContext ctx) {
        getCart(ctx.getPlayer()).ifPresent(AbstractDrawnEntity::toggleSlow);
    }

    public static Optional<AbstractDrawnEntity> getCart(final PlayerEntity player) {
        final Entity ridden = player.getRidingEntity();
        if (ridden == null) return Optional.empty();
        if (ridden instanceof AbstractDrawnEntity) return Optional.of((AbstractDrawnEntity) ridden);
        return AstikorWorld.get(ridden.world).resolve().flatMap(w -> w.getDrawn(ridden));
    }
}
