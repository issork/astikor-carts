package de.mennomax.astikorcarts.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import de.mennomax.astikorcarts.AstikorCarts;
import de.mennomax.astikorcarts.client.renderer.entity.model.PlowCartModel;
import de.mennomax.astikorcarts.entity.PlowCartEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@SuppressWarnings("deprecation")
public class PlowCartRenderer extends DrawnRenderer<PlowCartEntity, PlowCartModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AstikorCarts.ID, "textures/entity/plowcart.png");

    public PlowCartRenderer(final EntityRendererManager renderManager) {
        super(renderManager, new PlowCartModel());
        this.shadowSize = 1.0F;
    }

    @Override
    protected ResourceLocation getEntityTexture(final PlowCartEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void renderContents(final PlowCartEntity entity, final float delta) {
        super.renderContents(entity, delta);
        for (int i = 0; i < entity.inventory.getSlots(); i++) {
            final ItemStack stack = entity.getStackInSlot(i);
            if (stack.isEmpty()) {
                continue;
            }
            this.attach(this.model.getBody(), this.model.getShaft(i), () -> {
                GlStateManager.rotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.translated(-4.0D / 16.0D, 1.0D / 16.0D, 0.0D);
                if (stack.getItem() instanceof BlockItem) {
                    GlStateManager.translated(0.0D, -0.1D, 0.0D);
                    GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
                }
                Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
            }, 0.0625F);
        }
    }

    private void attach(final RendererModel bone, final RendererModel attachment, final Runnable function, final float scale) {
        GlStateManager.pushMatrix();
        bone.postRender(scale);
        if (bone == attachment) {
            function.run();
        } else if (bone.childModels != null) {
            for (final RendererModel child : bone.childModels) {
                this.attach(child, attachment, function, scale);
            }
        }
        GlStateManager.popMatrix();
    }
}
