package de.mennomax.astikorcarts.network.clientbound;

import de.mennomax.astikorcarts.client.sound.CartingJukeboxSound;
import de.mennomax.astikorcarts.entity.SupplyCartEntity;
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

public final class CartingJukeboxMessage implements Message {
    private int cartId;

    private MusicDiscItem disc;

    public CartingJukeboxMessage() {
    }

    public CartingJukeboxMessage(final SupplyCartEntity cart, final MusicDiscItem disc) {
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

    public static final class Handler implements BiConsumer<CartingJukeboxMessage, ClientMessageContext> {
        @Override
        public void accept(final CartingJukeboxMessage msg, final ClientMessageContext ctx) {
            final World world = ctx.getWorld();
            final Entity e = world.getEntityByID(msg.cartId);
            if (e instanceof SupplyCartEntity) {
                ctx.getMinecraft().getSoundHandler().play(new CartingJukeboxSound((SupplyCartEntity) e, msg.disc));
                if (e.getDistanceSq(ctx.getPlayer()) < 64.0D * 64.0D) {
                    ctx.getMinecraft().ingameGUI.func_238451_a_(msg.disc.getDescription());
                }
            }
        }
    }
}
