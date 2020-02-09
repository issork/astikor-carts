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
import net.minecraft.client.renderer.entity.HorseRenderer;
import net.minecraft.client.renderer.entity.model.HorseModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.lwjgl.opengl.GL11;

public class MobCartRenderer extends DrawnRenderer<MobCartEntity, MobCartModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AstikorCarts.ID, "textures/entity/mobcart.png");

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
            final HorseEntity horse = (HorseEntity) pulling;
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float) -TileEntityRendererDispatcher.staticPlayerX, (float) -TileEntityRendererDispatcher.staticPlayerY, (float) -TileEntityRendererDispatcher.staticPlayerZ);
            final Mat4f cmv = this.modelView(coachman, delta);
            final Mat4f pmv = this.modelView(pulling, delta);
            this.horseTransform(pmv, horse, delta);
            final float strength = Math.min(MathHelper.lerp(delta, horse.prevLimbSwingAmount, horse.limbSwingAmount), 1.0F);
            final float swing = horse.limbSwing - horse.limbSwingAmount * (1.0F - delta);
            for (int side = -1; side <= 1; side += 2) {
                final Vec4f start = new Vec4f(side * 0.4F, 1.17F + MathHelper.cos((swing - 1.02F) * 0.4F * 2) * 0.05F * strength, -0.3F, 1.0F).transform(cmv);
                final Vec4f end = new Vec4f(-side * 0.175F, -0.5F, -0.3F, 1.0F).transform(pmv);
                this.renderCurve(start.x(), start.y(), start.z(), end.x(), end.y(), end.z());
            }
            GlStateManager.popMatrix();
        }
    }

    private void horseTransform(final Mat4f m, final HorseEntity entity, final float delta) {
        final HorseModel<HorseEntity> horseModel = this.renderManager.<HorseEntity, HorseRenderer>getRenderer(entity).getEntityModel();
        float strength = 0.0F;
        float swing = 0.0F;
        if (!entity.isPassenger() && entity.isAlive()) {
            strength = MathHelper.lerp(delta, entity.prevLimbSwingAmount, entity.limbSwingAmount);
            swing = entity.limbSwing - entity.limbSwingAmount * (1.0F - delta);
            if (entity.isChild()) {
                swing *= 3.0F;
            }
            if (strength > 1.0F) {
                strength = 1.0F;
            }
        }
        horseModel.setLivingAnimations(entity, swing, strength, delta);
        final RendererModel head = ObfuscationReflectionHelper.getPrivateValue(HorseModel.class, horseModel, "field_217128_b");
        final Mat4f tmp = new Mat4f();
        m.mul(tmp.makeScale(-1.0F, -1.0F, 1.0F));
        m.mul(tmp.makeScale(1.1F, 1.1F, 1.1F));
        m.mul(tmp.makeTranslation(0.0F, -1.501F, 0.0F));
        this.transform(m, head);
    }

    private void transform(final Mat4f m, final RendererModel bone) {
        final Mat4f tmp = new Mat4f();
        m.mul(tmp.makeTranslation(bone.rotationPointX / 16.0F, bone.rotationPointY / 16.0F, bone.rotationPointZ / 16.0F));
        if (bone.rotateAngleZ != 0.0F) {
            m.mul(tmp.makeRotation(bone.rotateAngleZ, 0.0F, 0.0F, 1.0F));
        }
        if (bone.rotateAngleY != 0.0F) {
            m.mul(tmp.makeRotation(bone.rotateAngleY, 0.0F, 1.0F, 0.0F));
        }
        if (bone.rotateAngleX != 0.0F) {
            m.mul(tmp.makeRotation(bone.rotateAngleX, 1.0F, 0.0F, 0.0F));
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
        r.makeRotation((float) Math.toRadians(180.0F - MathHelper.func_219805_h(delta, prevYaw, yaw)), 0.0F, 1.0F, 0.0F);
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
        final float r0 = 97.0F / 255.0F;
        final float g0 = 58.0F / 255.0F;
        final float b0 = 29.0F / 255.0F;
        buf.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i <= n; i++) {
            float r = r0;
            float g = g0;
            float b = b0;
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
            float r = r0;
            float g = g0;
            float b = b0;
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
