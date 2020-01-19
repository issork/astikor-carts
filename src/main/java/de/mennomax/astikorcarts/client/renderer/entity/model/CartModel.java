package de.mennomax.astikorcarts.client.renderer.entity.model;

import de.mennomax.astikorcarts.entity.AbstractDrawnEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.util.math.MathHelper;

public abstract class CartModel<T extends AbstractDrawnEntity> extends EntityModel<T> {
    protected final RendererModel body;

    protected final RendererModel leftWheel;

    protected final RendererModel rightWheel;

    protected CartModel(final int textureWidth, final int textureHeight) {
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;

        this.body = new RendererModel(this);
        this.body.setRotationPoint(0.0F, -11.0F, 1.0F);
        this.leftWheel =  new RendererModel(this, 54, 50);
        this.leftWheel.setRotationPoint(14.5F, -11.0F, 1.0F);
        this.leftWheel.addBox(-2.0F, -1.0F, -1.0F, 2, 2, 2);
        for (int i = 0; i < 8; i++) {
            final RendererModel rim = new RendererModel(this, 58, 54);
            rim.addBox(-2.0F, -4.5F, 9.86F, 2, 9, 1);
            rim.rotateAngleX = i * (float) Math.PI / 4.0F;
            this.leftWheel.addChild(rim);

            final RendererModel spoke = new RendererModel(this, 54, 54);
            spoke.addBox(-1.5F, 1.0F, -0.5F, 1, 9, 1);
            spoke.rotateAngleX = i * (float) Math.PI / 4.0F;
            this.leftWheel.addChild(spoke);
        }

        this.rightWheel = new RendererModel(this, 54, 50);
        this.rightWheel.setRotationPoint(-14.5F, -11.0F, 1.0F);
        this.rightWheel.addBox(0.0F, -1.0F, -1.0F, 2, 2, 2);
        for (int i = 0; i < 8; i++) {
            final RendererModel rim = new RendererModel(this, 58, 54);
            rim.addBox(0.0F, -4.5F, 9.86F, 2, 9, 1);
            rim.rotateAngleX = i * (float) Math.PI / 4.0F;
            this.rightWheel.addChild(rim);

            final RendererModel spoke = new RendererModel(this, 54, 54);
            spoke.addBox(0.5F, 1.0F, -0.5F, 1, 9, 1);
            spoke.rotateAngleX = i * (float) Math.PI / 4.0F;
            this.rightWheel.addChild(spoke);
        }
    }

    @Override
    public void render(final T entity, final float delta, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float pitch, final float scale) {
        this.setRotationAngles(entity, delta, limbSwingAmount, ageInTicks, netHeadYaw, pitch, scale);
        this.body.render(scale);
        this.leftWheel.render(scale);
        this.rightWheel.render(scale);
    }

    @Override
    public void setRotationAngles(final T entity, final float delta, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float pitch, final float scale) {
        this.body.rotateAngleX = (float) Math.toRadians(pitch);
        this.rightWheel.rotateAngleX = (float) (entity.getWheelRotation(0) + entity.getWheelRotationIncrement(0) * delta);
        this.leftWheel.rotateAngleX = (float) (entity.getWheelRotation(1) + entity.getWheelRotationIncrement(1) * delta);
        final float time = entity.getTimeSinceHit() - delta;
        final float rot;
        if (time > 0.0F) {
            final float damage = Math.max(entity.getDamageTaken() - delta, 0.0F);
            rot = (float) Math.toRadians(MathHelper.sin(time) * time * damage / 40.0F * -entity.getForwardDirection());
        } else {
            rot = 0.0F;
        }
        this.rightWheel.rotateAngleZ = rot;
        this.leftWheel.rotateAngleZ = rot;
    }
}
