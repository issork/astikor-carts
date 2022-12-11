package de.mennomax.astikorcarts.client.renderer.texture;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public class AssembledTextureFactory {
    private final Object2ObjectMap<ResourceLocation, AssembledTexture> textures = new Object2ObjectOpenHashMap<>();

    public AssembledTextureFactory add(final ResourceLocation texture, final AssembledTexture assembled) {
        this.textures.put(texture, assembled);
        return this;
    }

    public void register(final IEventBus bus) {
        bus.addListener(this::bake);
    }

    private void bake(final ModelBakeEvent event) {
        final Minecraft mc = Minecraft.getInstance();
        final ResourceManager resources = mc.getResourceManager();
        final TextureManager textures = mc.getTextureManager();
        final ModelManager sprites = event.getModelManager();
        Object2ObjectMaps.fastForEach(this.textures, e -> {
            if (resources.hasResource(e.getKey())) {
                textures.release(e.getKey());
            } else {
                textures.register(e.getKey(), e.getValue().assemble(sprites));
            }
        });
    }
}
