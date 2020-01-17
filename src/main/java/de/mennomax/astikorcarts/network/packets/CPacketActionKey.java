package de.mennomax.astikorcarts.network.packets;

import de.mennomax.astikorcarts.AstikorCarts;
import de.mennomax.astikorcarts.entity.AbstractDrawnEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import java.util.List;
import java.util.function.Supplier;

public class CPacketActionKey {

    public static void encode(final CPacketActionKey packet, final PacketBuffer buffer) {

    }

    public static CPacketActionKey decode(final PacketBuffer buffer) {
        return new CPacketActionKey();
    }

    public static void handle(final CPacketActionKey msg, final Supplier<Context> ctx) {
        ctx.get().enqueueWork(() -> {
            final ServerPlayerEntity player = ctx.get().getSender();
            Entity pulling = player.isPassenger() ? player.getRidingEntity() : player;
            AbstractDrawnEntity closest = AstikorCarts.SERVERPULLMAP.get(pulling);
            if (closest == null) {
                final List<AbstractDrawnEntity> result = player.world.getEntitiesWithinAABB(AbstractDrawnEntity.class, player.getBoundingBox().grow(3), entity -> entity != player.getRidingEntity());
                if (result.isEmpty()) {
                    return;
                }
                closest = result.get(0);
                for (int i = 1; i < result.size(); i++) {
                    final AbstractDrawnEntity cart = result.get(i);
                    if (cart.getDistance(pulling) < closest.getDistance(pulling)) {
                        closest = cart;
                    }
                }
            } else {
                pulling = null;
            }
            closest.setPulling(pulling);
        });
        ctx.get().setPacketHandled(true);
    }

}
