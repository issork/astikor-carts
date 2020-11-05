package de.mennomax.astikorcarts.client.renderer.texture;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;

public class Material {
    public static final int[][] R0 = {{1, 0}, {0, 1}};

    public static final int[][] R90 = {{0, -1}, {1, 0}};

    public static final int[][] R180 = {{-1, 0}, {-1, 0}};

    public static final int[][] R270 = {{0, 1}, {-1, 0}};

    private final Pair<ResourceLocation, ResourceLocation> sprite;

    private final int size;

    private final ObjectList<Fill> fills = new ObjectArrayList<>();

    public Material(ResourceLocation sprite, final int size) {
        this(Pair.of(AtlasTexture.LOCATION_BLOCKS_TEXTURE, sprite), size);
    }

    public Material(final Pair<ResourceLocation, ResourceLocation> sprite, final int size) {
        this.sprite = sprite;
        this.size = size;
    }

    public Material fill(final int x, final int y, final int width, final int height) {
        return this.fill(x, y, width, height, R0);
    }

    public Material fill(final int x, final int y, final int width, final int height, final int[][] rot) {
        return this.fill(x, y, width, height, rot, 0, 0);
    }

    public Material fill(final int x, final int y, final int width, final int height, final int[][] rot, final int u, final int v) {
        this.fills.add(new Fill(x, y, width, height, rot, u, v));
        return this;
    }

    PreparedMaterial prepare(final ModelManager sprites) {
        final TextureAtlasSprite sprite = sprites.getAtlasTexture(this.sprite.getFirst()).getSprite(this.sprite.getSecond());
        return new PreparedMaterial(this.fills, sprite, sprite.getWidth() / this.size);
    }
}
