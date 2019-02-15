package de.mennomax.astikorcarts.client.render;

import de.mennomax.astikorcarts.AstikorCarts;
import de.mennomax.astikorcarts.client.model.ModelCargoCart;
import de.mennomax.astikorcarts.entity.EntityCargoCart;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderCargoCart extends Render<EntityCargoCart>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(AstikorCarts.MODID, "textures/entity/cargocart.png");
    protected ModelBase model = new ModelCargoCart();

    public RenderCargoCart(RenderManager renderManager)
    {
        super(renderManager);
        this.shadowSize = 1.0F;
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityCargoCart entity)
    {
        return TEXTURE;
    }

    @Override
    public void doRender(EntityCargoCart entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        GlStateManager.pushMatrix();
        this.setupTranslation(x, y, z);
        this.setupRotation(entityYaw);
        this.bindEntityTexture(entity);

        if (this.renderOutlines)
        {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
        }

        this.model.render(entity, partialTicks, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);

        if (this.renderOutlines)
        {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    public void setupRotation(float entityYaw)
    {
        GlStateManager.rotate(180.0F - entityYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.scale(-1.0F, -1.0F, 1.0F);
    }

    public void setupTranslation(double x, double y, double z)
    {
        GlStateManager.translate((float) x, (float) y + 1.0F, (float) z);
    }

}
