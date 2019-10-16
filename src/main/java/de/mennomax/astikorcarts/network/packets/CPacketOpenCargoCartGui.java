package de.mennomax.astikorcarts.network.packets;

import java.util.function.Supplier;

import de.mennomax.astikorcarts.entity.CargoCartEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class CPacketOpenCargoCartGui {

    private int cartId;

    public CPacketOpenCargoCartGui(int cartId) {
        this.cartId = cartId;
    }

    public static void encode(CPacketOpenCargoCartGui packet, PacketBuffer buffer) {
        buffer.writeInt(packet.cartId);
    }

    public static CPacketOpenCargoCartGui decode(PacketBuffer buffer) {
        return new CPacketOpenCargoCartGui(buffer.readInt());
    }

    public static void handle(CPacketOpenCargoCartGui msg, Supplier<Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            CargoCartEntity cart = (CargoCartEntity) player.world.getEntityByID(msg.cartId);
            cart.openContainer(player);
        });
        ctx.get().setPacketHandled(true);
    }

}
