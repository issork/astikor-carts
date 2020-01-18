package de.mennomax.astikorcarts.client.renderer.entity.model;

import de.mennomax.astikorcarts.entity.MobCartEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MobCartModel extends EntityModel<MobCartEntity> {
    private final RendererModel axis;
    private final RendererModel cartBase;
    private final RendererModel shaft;
    private final RendererModel boardLeft;
    private final RendererModel boardRight;
    private final RendererModel boardBack;
    private final RendererModel boardFront;
    private final RendererModel leftWheel;
    private final RendererModel rightWheel;
    private final RendererModel body;

    public MobCartModel() {
        this.textureWidth = 64;
        this.textureHeight = 64;

        this.axis = new RendererModel(this, 0, 21);
        this.axis.addBox(-12.5F, -1.0F, -1.0F, 25, 2, 2);

        this.cartBase = new RendererModel(this, 0, 0);
        this.cartBase.addBox(-15.5F, -10.0F, -2.0F, 29, 20, 1);
        this.cartBase.rotateAngleX = (float) -Math.PI / 2.0F;
        this.cartBase.rotateAngleY = (float) -Math.PI / 2.0F;

        this.shaft = new RendererModel(this, 0, 25);
        this.shaft.setRotationPoint(0.0F, -5.0F, -15.0F);
        this.shaft.rotateAngleY = (float) Math.PI / 2.0F;
        this.shaft.addBox(0.0F, -0.5F, -8.0F, 20, 2, 1);
        this.shaft.addBox(0.0F, -0.5F, 7.0F, 20, 2, 1);

        this.boardLeft = new RendererModel(this, 0, 28);
        this.boardLeft.addBox(-10.0F, -14.5F, 9F, 8, 31, 2);
        this.boardLeft.rotateAngleX = (float) -Math.PI / 2.0F;
        this.boardLeft.rotateAngleZ = (float) Math.PI / 2.0F;

        this.boardRight = new RendererModel(this, 0, 28);
        this.boardRight.addBox(-10.0F, -14.5F, -11F, 8, 31, 2);
        this.boardRight.rotateAngleX = (float) -Math.PI / 2.0F;
        this.boardRight.rotateAngleZ = (float) Math.PI / 2.0F;

        this.boardBack = new RendererModel(this, 20, 28);
        this.boardBack.addBox(-9F, -10.0F, 12.5F, 18, 8, 2);

        this.boardFront = new RendererModel(this, 20, 28);
        this.boardFront.addBox(-9F, -10.0F, -16.5F, 18, 8, 2);

        // --LEFT-WHEEL----------------------------------
        this.leftWheel = new RendererModel(this, 52, 21);
        this.leftWheel.setRotationPoint(14.5F, -11.0F, 1.0F);
        this.leftWheel.addBox(-2.0F, -1.0F, -1.0F, 2, 2, 2);
        for (int i = 0; i < 8; i++) {
            final RendererModel rim = new RendererModel(this, 20, 38);
            rim.addBox(-2.0F, -4.5F, 9.86F, 2, 9, 1);
            rim.rotateAngleX = i * (float) Math.PI / 4.0F;
            this.leftWheel.addChild(rim);

            final RendererModel spoke = new RendererModel(this, 20, 48);
            spoke.addBox(-1.5F, 1.0F, -0.5F, 1, 9, 1);
            spoke.rotateAngleX = i * (float) Math.PI / 4.0F;
            this.leftWheel.addChild(spoke);
        }

        // --RIGHT-WHEEL---------------------------------
        this.rightWheel = new RendererModel(this, 52, 21);
        this.rightWheel.mirror = true;
        this.rightWheel.setRotationPoint(-14.5F, -11.0F, 1.0F);
        this.rightWheel.addBox(0.0F, -1.0F, -1.0F, 2, 2, 2);
        for (int i = 0; i < 8; i++) {
            final RendererModel rim = new RendererModel(this, 20, 38);
            rim.addBox(0.0F, -4.5F, 9.86F, 2, 9, 1);
            rim.rotateAngleX = i * (float) Math.PI / 4.0F;
            this.rightWheel.addChild(rim);

            final RendererModel spoke = new RendererModel(this, 20, 48);
            spoke.addBox(0.5F, 1.0F, -0.5F, 1, 9, 1);
            spoke.rotateAngleX = i * (float) Math.PI / 4.0F;
            this.rightWheel.addChild(spoke);
        }

        this.body = new RendererModel(this);
        this.body.addChild(this.axis);
        this.body.addChild(this.cartBase);
        this.body.addChild(this.shaft);
        this.body.addChild(this.boardLeft);
        this.body.addChild(this.boardRight);
        this.body.addChild(this.boardBack);
        this.body.addChild(this.boardFront);
        this.body.setRotationPoint(0.0F, -11.0F, 1.0F);
    }

    @Override
    public void render(final MobCartEntity entityIn, final float delta, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float pitch, final float scale) {
        this.setRotationAngles(entityIn, delta, limbSwingAmount, ageInTicks, netHeadYaw, pitch, scale);
        this.rightWheel.render(scale);
        this.leftWheel.render(scale);
        this.body.render(scale);
    }

    @Override
    public void setRotationAngles(final MobCartEntity entity, final float delta, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float pitch, final float scale) {
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
