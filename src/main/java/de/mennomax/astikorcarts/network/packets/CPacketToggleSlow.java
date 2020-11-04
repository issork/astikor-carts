package de.mennomax.astikorcarts.network.packets;

import de.mennomax.astikorcarts.entity.AbstractDrawnEntity;
import de.mennomax.astikorcarts.network.Message;
import de.mennomax.astikorcarts.network.ServerMessageContext;
import de.mennomax.astikorcarts.world.AstikorWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
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
        final Entity pulling = getPulling(ctx.getPlayer());
        if (pulling instanceof LivingEntity) {
            final ModifiableAttributeInstance attr = ((LivingEntity) pulling).getAttribute(Attributes.MOVEMENT_SPEED);
            if (attr != null) {
                if (attr.hasModifier(AbstractDrawnEntity.PULL_SLOWLY_MODIFIER)) {
                    attr.removeModifier(AbstractDrawnEntity.PULL_SLOWLY_MODIFIER);
                } else {
                    attr.applyNonPersistentModifier(AbstractDrawnEntity.PULL_SLOWLY_MODIFIER);
                }
            }
        }
    }

    public static Entity getPulling(final PlayerEntity player) {
        final Entity ridden = player.getRidingEntity();
        if (ridden == null) {
            return null;
        }
        if (ridden instanceof AbstractDrawnEntity) {
            return ((AbstractDrawnEntity) ridden).getPulling();
        }
        if (AstikorWorld.stream(ridden.world).allMatch(w -> w.isPulling(ridden))) {
            return ridden;
        }
        return null;
    }
}
