package de.mennomax.astikorcarts.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;

import de.mennomax.astikorcarts.AstikorCarts;
import de.mennomax.astikorcarts.client.renderer.entity.model.PlowCartModel;
import de.mennomax.astikorcarts.entity.PlowCartEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

@SuppressWarnings("deprecation")
public class PlowCartRenderer extends EntityRenderer<PlowCartEntity>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(AstikorCarts.MODID, "textures/entity/plowcart.png");
    protected EntityModel<PlowCartEntity> model = new PlowCartModel();

    public PlowCartRenderer(EntityRendererManager renderManager)
    {
        super(renderManager);
        this.shadowSize = 1.0F;
    }

    @Override
    protected ResourceLocation getEntityTexture(PlowCartEntity entity) {
        return TEXTURE;
    }

    @Override
    public void doRender(PlowCartEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        this.setupTranslation(x, y, z);
        this.setupRotation(entityYaw);
        this.bindEntityTexture(entity);

        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(entity));
        }

        this.model.render(entity, partialTicks, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);

        if (this.renderOutlines) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
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
                GlStateManager.translated(x + (1.45D + offsetSides) * MathHelper.sin((-36.0F + entityYaw+i*36.0F) * 0.017453292F), y+0.10D, z - (1.45D + offsetSides) * MathHelper.cos((-36.0F + entityYaw+i*36.0F) * 0.017453292F));
                GlStateManager.rotatef(120.0F - entityYaw - 30.0F*i, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotatef(181.0F, 0.0F, 0.0F, 1.0F);
            }
            else
            {
                GlStateManager.translated(x + (1.9D + offsetSides) * MathHelper.sin((-34.7F + entityYaw+i*34.7F) * 0.017453292F), y+0.90D, z - (1.9D + offsetSides) * MathHelper.cos((-34.7F + entityYaw+i*34.7F) * 0.017453292F));
                GlStateManager.rotatef(120.0F - entityYaw - 30.0F*i, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotatef(207.0F, 0.0F, 0.0F, 1.0F);
            }
            Minecraft.getInstance().getItemRenderer().renderItem(entity.getTool(i), ItemCameraTransforms.TransformType.FIXED);
            GlStateManager.popMatrix();
        }
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    public void setupRotation(float entityYaw)
    {
        GlStateManager.rotatef(180.0F - entityYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
    }

    public void setupTranslation(double x, double y, double z)
    {
        GlStateManager.translated(x, y + 1.0D, z);
    }

}
