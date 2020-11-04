package de.mennomax.astikorcarts.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.mennomax.astikorcarts.entity.PostilionEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.Nullable;

public final class PostilionRenderer extends EntityRenderer<PostilionEntity> {
    public PostilionRenderer(final EntityRendererManager manager) {
        super(manager);
    }

    @Override
    public void render(final PostilionEntity postilion, final float yaw, final float delta, final MatrixStack stack, final IRenderTypeBuffer source, final int packedLight) {
        if (!postilion.isInvisible()) {
            stack.push();
            stack.rotate(Vector3f.YP.rotationDegrees(180.0F - yaw));
            final AxisAlignedBB bounds = postilion.getBoundingBox().offset(-postilion.getPosX(), -postilion.getPosY(), -postilion.getPosZ());
            WorldRenderer.drawBoundingBox(stack, source.getBuffer(RenderType.getLines()), bounds, 1.0F, 1.0F, 1.0F, 1.0F);
            stack.pop();
            super.render(postilion, yaw, delta, stack, source, packedLight);
        }
    }

    @Override
    protected boolean canRenderName(final PostilionEntity postilion) {
        return true;
    }

    @Nullable
    @Override
    public ResourceLocation getEntityTexture(final PostilionEntity postilion) {
        return null;
    }
}
