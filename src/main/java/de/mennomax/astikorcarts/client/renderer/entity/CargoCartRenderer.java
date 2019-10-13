package de.mennomax.astikorcarts.client.renderer.entity;

import de.mennomax.astikorcarts.AstikorCarts;
import de.mennomax.astikorcarts.client.renderer.entity.model.CargoCartModel;
import de.mennomax.astikorcarts.entity.CargoCartEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class CargoCartRenderer extends DrawnRenderer<CargoCartEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AstikorCarts.MODID, "textures/entity/cargocart.png");

    public CargoCartRenderer(EntityRendererManager renderManager) {
        super(renderManager, new CargoCartModel());
        this.shadowSize = 1.0F;
    }

    @Override
    protected ResourceLocation getEntityTexture(CargoCartEntity entity) {
        return TEXTURE;
    }

}
