package de.mennomax.astikorcarts.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import de.mennomax.astikorcarts.entity.AbstractDrawnEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.util.math.MathHelper;

public abstract class DrawnRenderer<T extends AbstractDrawnEntity, M extends EntityModel<T>> extends EntityRenderer<T> {

    protected M model;

    protected DrawnRenderer(final EntityRendererManager renderManager, final M model) {
        super(renderManager);
        this.model = model;
    }

    @Override
    public void doRender(final T entity, final double x, final double y, final double z, final float yaw, final float delta) {
        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y, z);
        final AbstractDrawnEntity.RenderInfo info = entity.getInfo(delta);
        this.setupRotation(entity, info.getYaw(), delta);
        this.bindEntityTexture(entity);

        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(entity));
        }

        this.model.render(entity, delta, 0.0F, 0.0F, 0.0F, info.getPitch(), 0.0625F);
        this.renderContents(entity, delta);

        if (this.renderOutlines) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, info.getYaw(), delta);
    }

    protected void renderContents(final T entity, final float delta) {
    }

    public void setupRotation(final T entity, final float entityYaw, final float delta) {
        GlStateManager.rotatef(180.0F - entityYaw, 0.0F, 1.0F, 0.0F);
        final float time = entity.getTimeSinceHit() - delta;
        if (time > 0.0F) {
            final double center = 1.2D;
            GlStateManager.translated(0.0D, center, 0.0D);
            final float damage = Math.max(entity.getDamageTaken() - delta, 0.0F);
            final float angle = MathHelper.sin(time) * time * damage / 60.0F;
            GlStateManager.rotatef(angle * entity.getForwardDirection(), 0.0F, 0.0F, 1.0F);
            GlStateManager.translated(0.0D, -center, 0.0D);
            GlStateManager.translated(0.0D, angle / 32.0F, 0.0D);
        }
        GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
    }

}
