package de.mennomax.astikorcarts.network.packets;

import de.mennomax.astikorcarts.AstikorCarts;
import de.mennomax.astikorcarts.entity.AbstractDrawnEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CPacketToggleSlow {

    public static void encode(final CPacketToggleSlow msg, final PacketBuffer buf) {

    }

    public static CPacketToggleSlow decode(final PacketBuffer buf) {
        return new CPacketToggleSlow();
    }

    public static void handle(final CPacketToggleSlow msg, final Supplier<NetworkEvent.Context> ctx) {
        final ServerPlayerEntity sender = ctx.get().getSender();
        ctx.get().enqueueWork(() -> {
            final Entity ridden = sender.getRidingEntity();
            if (AstikorCarts.SERVERPULLMAP.containsKey(ridden)) {
                if (ridden instanceof MobEntity) {
                    if (((MobEntity) ridden).getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).hasModifier(AbstractDrawnEntity.PULL_SLOWLY_MODIFIER)) {
                        ((MobEntity) ridden).getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(AbstractDrawnEntity.PULL_SLOWLY_MODIFIER);
                    } else {
                        ((MobEntity) ridden).getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).applyModifier(AbstractDrawnEntity.PULL_SLOWLY_MODIFIER);
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
