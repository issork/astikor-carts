package de.mennomax.astikorcarts.client.renderer.entity;

import de.mennomax.astikorcarts.AstikorCarts;
import de.mennomax.astikorcarts.client.renderer.entity.model.MobCartModel;
import de.mennomax.astikorcarts.entity.MobCartEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class MobCartRenderer extends DrawnRenderer<MobCartEntity, MobCartModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AstikorCarts.MODID, "textures/entity/mobcart.png");

    public MobCartRenderer(final EntityRendererManager renderManager) {
        super(renderManager, new MobCartModel());
        this.shadowSize = 1.0F;
    }

    @Override
    protected ResourceLocation getEntityTexture(final MobCartEntity entity) {
        return TEXTURE;
    }

}
