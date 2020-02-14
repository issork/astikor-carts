package de.mennomax.astikorcarts.client.renderer.entity;

import de.mennomax.astikorcarts.AstikorCarts;
import de.mennomax.astikorcarts.client.renderer.entity.model.CargoCartModel;
import de.mennomax.astikorcarts.entity.CargoCartEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public final class CargoCartRenderer extends DrawnRenderer<CargoCartEntity, CargoCartModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AstikorCarts.ID, "textures/entity/cargocart.png");

    public CargoCartRenderer(final EntityRendererManager renderManager) {
        super(renderManager, new CargoCartModel());
        this.shadowSize = 1.0F;
    }

    @Override
    protected ResourceLocation getEntityTexture(final CargoCartEntity entity) {
        return TEXTURE;
    }
}
