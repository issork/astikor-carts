package de.mennomax.astikorcarts.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import de.mennomax.astikorcarts.entity.PostilionEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class PostilionRenderer extends EntityRenderer<PostilionEntity> {
    public PostilionRenderer(final EntityRendererManager manager) {
        super(manager);
    }

    @Override
    public void doRender(final PostilionEntity postilion, final double x, final double y, final double z, final float yaw, final float delta) {
        if (!postilion.isInvisible()) {
            GlStateManager.pushMatrix();
            GlStateManager.translated(x, y, z);
            GlStateManager.rotatef(180.0F - yaw, 0.0F, 1.0F, 0.0F);
            renderOffsetAABB(postilion.getBoundingBox(), -postilion.posX, -postilion.posY, -postilion.posZ);
            GlStateManager.popMatrix();
            super.doRender(postilion, x, y, z, yaw, delta);
        }
    }

    @Override
    protected boolean canRenderName(final PostilionEntity postilion) {
        return true;
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(final PostilionEntity postilion) {
        return null;
    }
}
