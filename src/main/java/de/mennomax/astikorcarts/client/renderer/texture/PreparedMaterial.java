package de.mennomax.astikorcarts.client.renderer.texture;

import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class PreparedMaterial {
    private final ObjectList<Fill> fills;

    private final TextureAtlasSprite sprite;

    private final int resolution;

    PreparedMaterial(final ObjectList<Fill> fills, final TextureAtlasSprite sprite, final int resolution) {
        this.fills = fills;
        this.sprite = sprite;
        this.resolution = resolution;
    }

    int getResolution() {
        return this.resolution;
    }

    void draw(final NativeImage image, final int resolution) {
        for (final Fill m : this.fills) {
            m.fill(image, this.sprite, this.resolution, resolution);
        }
    }
}
