package de.mennomax.astikorcarts.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import de.mennomax.astikorcarts.AstikorCarts;
import de.mennomax.astikorcarts.client.renderer.entity.model.MobCartModel;
import de.mennomax.astikorcarts.entity.MobCartEntity;
import de.mennomax.astikorcarts.util.Mat4f;
import de.mennomax.astikorcarts.util.Vec4f;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class MobCartRenderer extends DrawnRenderer<MobCartEntity, MobCartModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AstikorCarts.MODID, "textures/entity/mobcart.png");

    public MobCartRenderer(final EntityRendererManager renderManager) {
        super(renderManager, new MobCartModel());
        this.shadowSize = 1.0F;
    }

    @Override
    public void doRender(final MobCartEntity entity, final double x, final double y, final double z, final float yaw, final float delta) {
        super.doRender(entity, x, y, z, yaw, delta);
        final LivingEntity coachman = entity.getControllingPassenger();
        final Entity pulling = entity.getPulling();
        if (pulling instanceof HorseEntity && coachman instanceof PlayerEntity) {
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float) -TileEntityRendererDispatcher.staticPlayerX, (float) -TileEntityRendererDispatcher.staticPlayerY, (float) -TileEntityRendererDispatcher.staticPlayerZ);
            final Mat4f cmv = this.modelView(coachman, delta);
            final Mat4f pmv = this.modelView(pulling, delta);
            for (int side = -1; side <= 1; side += 2) {
                final Vec4f start = new Vec4f(side * 0.4F, coachman.getHeight() * 0.65F, -0.3F, 1.0F).transform(cmv);
                final Vec4f end = new Vec4f(side * 0.25F, pulling.getHeight() * 1.05F, -1.25F, 1.0F).transform(pmv);
                this.renderCurve(start.x(), start.y(), start.z(), end.x(), end.y(), end.z());
            }
            GlStateManager.popMatrix();
        }
    }

    private Mat4f modelView(final Entity entity, final float delta) {
        final Mat4f m = new Mat4f();
        m.makeTranslation(
            (float) MathHelper.lerp(delta, entity.lastTickPosX, entity.posX),
            (float) MathHelper.lerp(delta, entity.lastTickPosY, entity.posY),
            (float) MathHelper.lerp(delta, entity.lastTickPosZ, entity.posZ));
        final Mat4f r = new Mat4f();
        final float prevYaw, yaw;
        if (entity instanceof LivingEntity) {
            prevYaw = ((LivingEntity) entity).prevRenderYawOffset;
            yaw = ((LivingEntity) entity).renderYawOffset;
        } else {
            prevYaw = entity.prevRotationYaw;
            yaw = entity.rotationYaw;
        }
        r.makeRotation(0.0F, 1.0F, 0.0F, (float) Math.toRadians(180.0F - MathHelper.func_219805_h(delta, prevYaw, yaw)));
        m.mul(r);
        return m;
    }

    private void renderCurve(final double x0, final double y0, final double z0, final double x1, final double y1, final double z1) {
        if (y0 > y1) {
            this.renderLead(x1, y1, z1, x0 - x1, y0 - y1, z0 - z1, 1);
        } else {
            this.renderLead(x0, y0, z0, x1 - x0, y1 - y0, z1 - z0, 0);
        }
    }

    private void renderLead(final double x, final double y, final double z, final double dx, final double dy, final double dz, final int offset) {
        final Tessellator tes = Tessellator.getInstance();
        final BufferBuilder buf = tes.getBuffer();
        GlStateManager.disableTexture();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        final int n = 24;
        final double w = 0.025D * 2;
        final double m = Math.sqrt(dx * dx + dz * dz);
        final double nx = dx / m;
        final double nz = dz / m;
        buf.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i <= n; i++) {
            float r = 0.5F;
            float g = 0.4F;
            float b = 0.3F;
            if ((i + offset) % 2 == 0) {
                r *= 0.7F;
                g *= 0.7F;
                b *= 0.7F;
            }
            final float t = (float) i / n;
            buf.pos(x + dx * t, y + dy * (t * t + t) * 0.5D - w, z + dz * t).color(r, g, b, 1.0F).endVertex();
            buf.pos(x + dx * t, y + dy * (t * t + t) * 0.5D + w, z + dz * t).color(r, g, b, 1.0F).endVertex();
        }
        tes.draw();
        buf.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i <= n; i++) {
            float r = 0.5F;
            float g = 0.4F;
            float b = 0.3F;
            if ((i + offset) % 2 == 0) {
                r *= 0.7F;
                g *= 0.7F;
                b *= 0.7F;
            }
            final float t = (float) i / n;
            buf.pos(x + dx * t + w * -nz, y + dy * (t * t + t) * 0.5D, z + dz * t + w * nx).color(r, g, b, 1.0F).endVertex();
            buf.pos(x + dx * t + w * nz, y + dy * (t * t + t) * 0.5D, z + dz * t + w * -nx).color(r, g, b, 1.0F).endVertex();
        }
        tes.draw();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture();
        GlStateManager.enableCull();
    }

    @Override
    protected ResourceLocation getEntityTexture(final MobCartEntity entity) {
        return TEXTURE;
    }
}
