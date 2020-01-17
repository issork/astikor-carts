package de.mennomax.astikorcarts.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import de.mennomax.astikorcarts.AstikorCarts;
import de.mennomax.astikorcarts.client.renderer.entity.model.PlowCartModel;
import de.mennomax.astikorcarts.entity.PlowCartEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

@SuppressWarnings("deprecation")
public class PlowCartRenderer extends DrawnRenderer<PlowCartEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AstikorCarts.MODID, "textures/entity/plowcart.png");

    public PlowCartRenderer(final EntityRendererManager renderManager) {
        super(renderManager, new PlowCartModel());
        this.shadowSize = 1.0F;
    }

    @Override
    protected ResourceLocation getEntityTexture(final PlowCartEntity entity) {
        return TEXTURE;
    }

    @Override
    public void doRender(final PlowCartEntity entity, final double x, final double y, final double z, final float entityYaw, final float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        // Render the items on the plow
        for (int i = 0; i < entity.inventory.getSlots(); i++) {
            GlStateManager.pushMatrix();
            final double offsetSides = 0.1D * ((i + 1) & 1);
            if (entity.getPlowing()) {
                GlStateManager.translated(x + (1.45D + offsetSides) * MathHelper.sin((-36.0F + entityYaw + i * 36.0F) * 0.017453292F), y + 0.10D, z - (1.45D + offsetSides) * MathHelper.cos((-36.0F + entityYaw + i * 36.0F) * 0.017453292F));
                GlStateManager.rotatef(120.0F - entityYaw - 30.0F * i, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotatef(181.0F, 0.0F, 0.0F, 1.0F);
            } else {
                GlStateManager.translated(x + (1.9D + offsetSides) * MathHelper.sin((-34.7F + entityYaw + i * 34.7F) * 0.017453292F), y + 0.90D, z - (1.9D + offsetSides) * MathHelper.cos((-34.7F + entityYaw + i * 34.7F) * 0.017453292F));
                GlStateManager.rotatef(120.0F - entityYaw - 30.0F * i, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotatef(207.0F, 0.0F, 0.0F, 1.0F);
            }
            final ItemStack stack = entity.getStackInSlot(i);
            if (stack.getItem() instanceof BlockItem) {
                GlStateManager.translated(0.0D, -0.1D, 0.0D);
                GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
            }
            Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
            GlStateManager.popMatrix();
        }
    }

}
