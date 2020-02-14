package de.mennomax.astikorcarts.network.packets;

import de.mennomax.astikorcarts.entity.AbstractDrawnEntity;
import de.mennomax.astikorcarts.network.Message;
import de.mennomax.astikorcarts.network.ServerMessageContext;
import de.mennomax.astikorcarts.world.AstikorWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;

public final class CPacketToggleSlow implements Message {
    @Override
    public void encode(final PacketBuffer buf) {
    }

    @Override
    public void decode(final PacketBuffer buf) {
    }

    public static void handle(final CPacketToggleSlow msg, final ServerMessageContext ctx) {
        final PlayerEntity sender = ctx.getPlayer();
        final Entity ridden = sender.getRidingEntity();
        if (ridden instanceof MobEntity && AstikorWorld.get(ridden.world).map(w -> w.isPulling(ridden)).orElse(false)) {
            if (((MobEntity) ridden).getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).hasModifier(AbstractDrawnEntity.PULL_SLOWLY_MODIFIER)) {
                ((MobEntity) ridden).getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(AbstractDrawnEntity.PULL_SLOWLY_MODIFIER);
            } else {
                ((MobEntity) ridden).getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).applyModifier(AbstractDrawnEntity.PULL_SLOWLY_MODIFIER);
            }
        }
    }
}
