package de.mennomax.astikorcarts.client.renderer.texture;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.Texture;

public class AssembledTexture {
    private final int width, height;

    private final ObjectList<Material> materials = new ObjectArrayList<>();

    public AssembledTexture(final int width, final int height) {
        this.width = width;
        this.height = height;
    }

    public AssembledTexture add(final Material material) {
        this.materials.add(material);
        return this;
    }

    Texture assemble(final ModelManager sprites) {
        final PreparedMaterial[] prepared = new PreparedMaterial[this.materials.size()];
        int resolution = 1;
        for (final ObjectListIterator<Material> it = this.materials.iterator(); it.hasNext(); ) {
            final int i = it.nextIndex();
            final PreparedMaterial p = it.next().prepare(sprites);
            prepared[i] = p;
            resolution = Math.max(resolution, p.getResolution());
        }
        final NativeImage image = new NativeImage(this.width * resolution, this.height * resolution, true);
        for (final PreparedMaterial p : prepared) p.draw(image, resolution);
        return new DynamicTexture(image);
    }
}
