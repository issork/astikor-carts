package de.mennomax.astikorcarts.client.sound;

import de.mennomax.astikorcarts.entity.CargoCartEntity;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.util.SoundCategory;

public class CartingJukeboxSound extends TickableSound {
    private final CargoCartEntity cart;

    public CartingJukeboxSound(final CargoCartEntity cart, final MusicDiscItem disc) {
        super(disc.getSound(), SoundCategory.RECORDS);
        this.cart = cart;
    }

    @Override
    public void tick() {
        if (this.cart.isAlive() && this.cart.hasJukebox()) {
            this.x = (float) this.cart.getPosX();
            this.y = (float) this.cart.getPosY();
            this.z = (float) this.cart.getPosZ();
        } else {
            this.finishPlaying();
        }
    }
}
