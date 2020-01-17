package de.mennomax.astikorcarts.network.packets;

import de.mennomax.astikorcarts.entity.AbstractDrawnEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class SPacketDrawnUpdate {

    private final int pullingId;
    private final int cartId;

    public SPacketDrawnUpdate(final int pullingId, final int cartId) {
        this.pullingId = pullingId;
        this.cartId = cartId;
    }

    public static void encode(final SPacketDrawnUpdate msg, final PacketBuffer buf) {
        buf.writeInt(msg.pullingId);
        buf.writeInt(msg.cartId);
    }

    public static SPacketDrawnUpdate decode(final PacketBuffer buf) {
        return new SPacketDrawnUpdate(buf.readInt(), buf.readInt());
    }

    public static void handle(final SPacketDrawnUpdate msg, final Supplier<Context> ctx) {
        ctx.get().enqueueWork(() -> {
            final AbstractDrawnEntity cart = (AbstractDrawnEntity) Minecraft.getInstance().world.getEntityByID(msg.cartId);
            if (msg.pullingId < 0) {
                cart.setPulling(null);
            } else {
                cart.setPulling(Minecraft.getInstance().world.getEntityByID(msg.pullingId));
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
