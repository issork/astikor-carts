package de.mennomax.astikorcarts.network.packets;

import de.mennomax.astikorcarts.entity.CargoCartEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class CPacketOpenCargoCartGui {

    private final int cartId;

    public CPacketOpenCargoCartGui(final int cartId) {
        this.cartId = cartId;
    }

    public static void encode(final CPacketOpenCargoCartGui packet, final PacketBuffer buffer) {
        buffer.writeInt(packet.cartId);
    }

    public static CPacketOpenCargoCartGui decode(final PacketBuffer buffer) {
        return new CPacketOpenCargoCartGui(buffer.readInt());
    }

    public static void handle(final CPacketOpenCargoCartGui msg, final Supplier<Context> ctx) {
        ctx.get().enqueueWork(() -> {
            final ServerPlayerEntity player = ctx.get().getSender();
            final CargoCartEntity cart = (CargoCartEntity) player.world.getEntityByID(msg.cartId);
            cart.openContainer(player);
        });
        ctx.get().setPacketHandled(true);
    }

}
