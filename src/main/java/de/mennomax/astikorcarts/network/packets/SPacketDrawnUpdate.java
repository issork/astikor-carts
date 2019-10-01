package de.mennomax.astikorcarts.network.packets;

import java.util.function.Supplier;

import de.mennomax.astikorcarts.entity.AbstractDrawnEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SPacketDrawnUpdate {

    private final int pullingId;
    private final int cartId;

    public SPacketDrawnUpdate(int pullingId, int cartId) {
        this.pullingId = pullingId;
        this.cartId = cartId;
    }

    public static void encode(SPacketDrawnUpdate msg, PacketBuffer buf) {
        buf.writeInt(msg.pullingId);
        buf.writeInt(msg.cartId);
    }

    public static SPacketDrawnUpdate decode(PacketBuffer buf) {
        return new SPacketDrawnUpdate(buf.readInt(), buf.readInt());
    }

    public static void handle(SPacketDrawnUpdate msg, Supplier<Context> ctx) {
        ctx.get().enqueueWork(() -> {
            AbstractDrawnEntity cart = (AbstractDrawnEntity) Minecraft.getInstance().world.getEntityByID(msg.cartId);
            if (msg.pullingId < 0) {
                cart.setPulling(null);
            } else {
                cart.setPulling(Minecraft.getInstance().world.getEntityByID(msg.pullingId));
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
