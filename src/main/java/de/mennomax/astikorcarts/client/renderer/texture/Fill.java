package de.mennomax.astikorcarts.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class Fill {
    private final int x, y, width, height;

    private final int[][] rot;

    private final int u, v;

    Fill(final int x, final int y, final int width, final int height, final int[][] rot, final int u, final int v) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.rot = rot;
        this.u = u;
        this.v = v;
    }

    void fill(final NativeImage image, final TextureAtlasSprite sprite, final int resolution, final int outResolution) {
        final int r = outResolution / resolution;
        final int x1 = (this.x + this.width) * resolution;
        final int y1 = (this.y + this.height) * resolution;
        final int u0 = this.u * resolution;
        final int v0 = this.v * resolution;
        for (int y = this.y * resolution; y < y1; y++) {
            for (int dy = 0; dy < r; dy++) {
                for (int x = this.x * resolution; x < x1; x++) {
                    for (int dx = 0; dx < r; dx++) {
                        final int u1 = x * this.rot[0][0] + y * this.rot[0][1] + Math.min(this.rot[0][0], this.rot[0][1]) + u0;
                        final int v1 = x * this.rot[1][0] + y * this.rot[1][1] + Math.min(this.rot[1][0], this.rot[1][1]) + v0;
                        final int rgba = sprite.getPixelRGBA(0, Math.floorMod(u1, sprite.getWidth()), Math.floorMod(v1, sprite.getHeight()));
                        image.setPixelRGBA(r * x + dx, r * y + dy, rgba);
                    }
                }
            }
        }
    }
}
