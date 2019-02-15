package de.mennomax.astikorcarts.client.render;

import de.mennomax.astikorcarts.AstikorCarts;
import de.mennomax.astikorcarts.client.model.ModelPlowCart;
import de.mennomax.astikorcarts.entity.EntityPlowCart;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RenderPlowCart extends Render<EntityPlowCart>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(AstikorCarts.MODID, "textures/entity/plowcart.png");
    protected ModelBase model = new ModelPlowCart();

    public RenderPlowCart(RenderManager renderManager)
    {
        super(renderManager);
        this.shadowSize = 0.6F;
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityPlowCart entity)
    {
        return TEXTURE;
    }

    @Override
    public void doRender(EntityPlowCart entity, double x, double y, double z, float entityYaw, float partialTicks)
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
        
        // Render the tools on the plow
        for (int i = 0; i < entity.inventory.getSizeInventory(); i++)
        {
            GlStateManager.pushMatrix();
            double offsetSides = 0.1D * ((i+1) & 1);
            if (entity.getPlowing())
            {
                GlStateManager.translate(x + (1.45D + offsetSides) * MathHelper.sin((-36.0F + entityYaw+i*36.0F) * 0.017453292F), y+0.10D, z - (1.45D + offsetSides) * MathHelper.cos((-36.0F + entityYaw+i*36.0F) * 0.017453292F));
                GlStateManager.rotate(120.0F - entityYaw - 30.0F*i, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(181.0F, 0.0F, 0.0F, 1.0F);
            }
            else
            {
                GlStateManager.translate(x + (1.9D + offsetSides) * MathHelper.sin((-34.7F + entityYaw+i*34.7F) * 0.017453292F), y+0.90D, z - (1.9D + offsetSides) * MathHelper.cos((-34.7F + entityYaw+i*34.7F) * 0.017453292F));
                GlStateManager.rotate(120.0F - entityYaw - 30.0F*i, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(207.0F, 0.0F, 0.0F, 1.0F);
            }
            Minecraft.getMinecraft().getRenderItem().renderItem(((EntityPlowCart) entity).getTool(i), ItemCameraTransforms.TransformType.FIXED);
            GlStateManager.popMatrix();
        }
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    public void setupRotation(float entityYaw)
    {
        GlStateManager.rotate(180.0F - entityYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.scale(-1.0F, -1.0F, 1.0F);
    }

    public void setupTranslation(double x, double y, double z)
    {
        GlStateManager.translate(x, y + 1.0D, z);
    }

}
