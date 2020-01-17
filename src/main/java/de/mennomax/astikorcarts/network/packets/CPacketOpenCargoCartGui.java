package de.mennomax.astikorcarts.network.packets;

import de.mennomax.astikorcarts.entity.CargoCartEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class CPacketOpenCargoCartGui {

    public static void encode(final CPacketOpenCargoCartGui packet, final PacketBuffer buffer) {
    }

    public static CPacketOpenCargoCartGui decode(final PacketBuffer buffer) {
        return new CPacketOpenCargoCartGui();
    }

    public static void handle(final CPacketOpenCargoCartGui msg, final Supplier<Context> ctx) {
        ctx.get().enqueueWork(() -> {
            final ServerPlayerEntity player = ctx.get().getSender();
            final Entity ridden = player.getRidingEntity();
            if (ridden instanceof CargoCartEntity) {
                ((CargoCartEntity) ridden).openContainer(player);
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
