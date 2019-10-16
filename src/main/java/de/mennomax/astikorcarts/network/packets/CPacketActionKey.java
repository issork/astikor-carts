package de.mennomax.astikorcarts.network.packets;

import java.util.List;
import java.util.function.Supplier;

import de.mennomax.astikorcarts.AstikorCarts;
import de.mennomax.astikorcarts.entity.AbstractDrawnEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class CPacketActionKey {

    public static void encode(CPacketActionKey packet, PacketBuffer buffer) {

    }

    public static CPacketActionKey decode(PacketBuffer buffer) {
        return new CPacketActionKey();
    }

    public static void handle(CPacketActionKey msg, Supplier<Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            Entity pulling = player.isPassenger() ? player.getRidingEntity() : player;
            AbstractDrawnEntity closest = AstikorCarts.SERVERPULLMAP.get(pulling);
            if (closest == null) {
                List<AbstractDrawnEntity> result = player.world.getEntitiesWithinAABB(AbstractDrawnEntity.class, player.getBoundingBox().grow(3), entity -> entity != player.getRidingEntity());
                if (result.isEmpty()) {
                    return;
                }
                closest = result.get(0);
                for (int i = 1; i < result.size(); i++) {
                    AbstractDrawnEntity cart = result.get(i);
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
