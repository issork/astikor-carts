package de.mennomax.astikorcarts.network.packets;

import de.mennomax.astikorcarts.entity.AbstractDrawnEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import java.util.Optional;
import java.util.function.Supplier;

public class SPacketDrawnUpdate {

    private final int pullingId;
    private final int cartId;

    public SPacketDrawnUpdate(final int pullingId, final int cartId) {
        this.pullingId = pullingId;
        this.cartId = cartId;
    }

    public static void encode(final SPacketDrawnUpdate msg, final PacketBuffer buf) {
        buf.writeVarInt(msg.pullingId);
        buf.writeVarInt(msg.cartId);
    }

    public static SPacketDrawnUpdate decode(final PacketBuffer buf) {
        return new SPacketDrawnUpdate(buf.readVarInt(), buf.readVarInt());
    }

    public static void handle(final SPacketDrawnUpdate msg, final Supplier<Context> ctx) {
        ctx.get().enqueueWork(() -> {
            final Optional<ClientWorld> world = LogicalSidedProvider.CLIENTWORLD.get(ctx.get().getDirection().getReceptionSide());
            world.ifPresent(w -> {
                final Entity e = w.getEntityByID(msg.cartId);
                if (e instanceof AbstractDrawnEntity) {
                    if (msg.pullingId < 0) {
                        ((AbstractDrawnEntity) e).setPulling(null);
                    } else {
                        ((AbstractDrawnEntity) e).setPulling(w.getEntityByID(msg.pullingId));
                    }
                }
            });
        });
        ctx.get().setPacketHandled(true);
    }

}
