package de.mennomax.astikorcarts.client.sound;

import de.mennomax.astikorcarts.entity.SupplyCartEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.RecordItem;

public class CartingJukeboxSound extends AbstractTickableSoundInstance {
    private final SupplyCartEntity cart;

    private final RecordItem disc;

    public CartingJukeboxSound(final SupplyCartEntity cart, final RecordItem disc) {
        super(disc.getSound(), SoundSource.RECORDS);
        this.cart = cart;
        this.disc = disc;
    }

    @Override
    public void tick() {
        if (this.cart.isAlive() && getDisc(this.cart.getDisc()) == this.disc) {
            this.x = (float) this.cart.getX();
            this.y = (float) this.cart.getY();
            this.z = (float) this.cart.getZ();
        } else {
            this.stop();
        }
    }

    public static void play(final SupplyCartEntity e, final ItemStack stack) {
        final RecordItem disc = getDisc(stack);
        final Minecraft mc = Minecraft.getInstance();
        final LocalPlayer player = mc.player;
        if (player == null) return;
        mc.getSoundManager().play(new CartingJukeboxSound(e, disc));
        if (e.distanceToSqr(player) < 64.0D * 64.0D) {
            mc.gui.setNowPlaying(disc.getDisplayName());
        }
    }

    public static RecordItem getDisc(final ItemStack stack) {
        final Item item = stack.getItem();
        return item instanceof RecordItem recordItem ? recordItem : (RecordItem) Items.MUSIC_DISC_11;
    }
}
