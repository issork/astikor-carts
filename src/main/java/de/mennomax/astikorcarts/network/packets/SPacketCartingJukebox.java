package de.mennomax.astikorcarts.network.packets;

import de.mennomax.astikorcarts.client.sound.CartingJukeboxSound;
import de.mennomax.astikorcarts.entity.CargoCartEntity;
import de.mennomax.astikorcarts.network.ClientMessageContext;
import de.mennomax.astikorcarts.network.Message;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.BiConsumer;

public class SPacketCartingJukebox implements Message {
    private int cartId;

    private MusicDiscItem disc;

    public SPacketCartingJukebox() {
    }

    public SPacketCartingJukebox(final CargoCartEntity cart, final MusicDiscItem disc) {
        this.cartId = cart.getEntityId();
        this.disc = disc;
    }

    @Override
    public void encode(final PacketBuffer buf) {
        buf.writeVarInt(this.cartId);
        buf.writeRegistryIdUnsafe(ForgeRegistries.ITEMS, this.disc);
    }

    @Override
    public void decode(final PacketBuffer buf) {
        this.cartId = buf.readVarInt();
        final Item item = buf.readRegistryIdUnsafe(ForgeRegistries.ITEMS);
        this.disc = (MusicDiscItem) (item instanceof MusicDiscItem ? item : Items.MUSIC_DISC_11);
    }

    public static final class Handler implements BiConsumer<SPacketCartingJukebox, ClientMessageContext> {
        @Override
        public void accept(final SPacketCartingJukebox msg, final ClientMessageContext ctx) {
            final World world = ctx.getWorld();
            final Entity e = world.getEntityByID(msg.cartId);
            if (e instanceof CargoCartEntity) {
                ctx.getMinecraft().getSoundHandler().play(new CartingJukeboxSound((CargoCartEntity) e, msg.disc));
            }
        }
    }
}