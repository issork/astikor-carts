package de.mennomax.astikorcarts.client.renderer.entity.model;

import de.mennomax.astikorcarts.entity.PlowCartEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.RendererModel;

public class PlowCartModel extends EntityModel<PlowCartEntity> {
    private final RendererModel axis;
    private final RendererModel[] triangle = new RendererModel[3];
    private final RendererModel shaft;
    private final RendererModel shaftConnector;
    private final RendererModel[] plowShaftUpper = new RendererModel[3];
    private final RendererModel[] plowShaftLower = new RendererModel[3];
    private final RendererModel plowHandle;
    private final RendererModel plowHandleGrip;
    private final RendererModel leftWheel;
    private final RendererModel rightWheel;
    private final RendererModel body;
    private final RendererModel parts;
    private final RendererModel shafts;

    public PlowCartModel() {
        // --AXIS--------------------------------------
        this.axis = new RendererModel(this, 0, 0);
        this.axis.addBox(-12.5F, -1.0F, -1.0F, 25, 2, 2);

        // --BOTTOM-BOARD--------------------------------------
        this.triangle[0] = new RendererModel(this, 0, 4);
        this.triangle[0].addBox(-7.5F, -9.0F, 0.0F, 15, 2, 2);

        this.triangle[1] = new RendererModel(this, 0, 11);
        this.triangle[1].addBox(-5.0F, -9.0F, 0.5F, 2, 14, 2);
        this.triangle[1].rotateAngleZ = -0.175F;

        this.triangle[2] = new RendererModel(this, 0, 11);
        this.triangle[2].addBox(3.0F, -9.0F, 0.5F, 2, 14, 2);
        this.triangle[2].rotateAngleZ = 0.175F;
        this.triangle[2].mirror = true;

        // --Horse shafts--------------------------------------
        this.shaft = new RendererModel(this, 0, 8);
        this.shaft.rotateAngleZ = -0.07F;
        this.shaft.addBox(0.0F, 0.0F, -8.0F, 20, 2, 1);
        this.shaft.addBox(0.0F, 0.0F, 7.0F, 20, 2, 1);

        this.shaftConnector = new RendererModel(this, 0, 27);
        this.shaftConnector.rotateAngleZ = -0.26F;
        this.shaftConnector.addBox(-16.0F, 0.0F, -8.0F, 16, 2, 1);
        this.shaftConnector.addBox(-16.0F, 0.0F, 7.0F, 16, 2, 1);

        this.shafts = new RendererModel(this);
        this.shafts.setRotationPoint(0.0F, 0.0F, -14.0F);
        this.shafts.rotateAngleY = (float) Math.PI / 2.0F;
        this.shafts.addChild(this.shaft);
        this.shafts.addChild(this.shaftConnector);

        // --PLOW-SHAFT---------------------------------
        for (int i = 0; i < this.plowShaftUpper.length; i++) {
            this.plowShaftUpper[i] = new RendererModel(this, 56, 0);
            this.plowShaftUpper[i].addBox(-1.0F, -2.0F, -2.0F, 2, 30, 2);
            this.plowShaftUpper[i].setRotationPoint(-3.0F + 3 * i, -7.0F, 0.0F);
            this.plowShaftUpper[i].rotateAngleY = -0.523599F + (float) Math.PI / 6.0F * i;

            this.plowShaftLower[i] = new RendererModel(this, 42, 4);
            this.plowShaftLower[i].addBox(-1.0F, -0.7F, -0.7F, 2, 10, 2);
            this.plowShaftLower[i].setRotationPoint(0.0F, 28.0F, -1.0F);
            this.plowShaftLower[i].rotateAngleX = (float) Math.PI / 4.0F;
            this.plowShaftUpper[i].addChild(this.plowShaftLower[i]);
        }

        this.plowHandle = new RendererModel(this, 50, 4);
        this.plowHandle.addBox(-0.5F, 0.0F, -0.5F, 1, 18, 1);
        this.plowHandle.setRotationPoint(0.0F, 33.0F, 5.0F);
        this.plowHandle.rotateAngleX = (float) Math.PI / 2.0F;
        this.plowShaftUpper[1].addChild(this.plowHandle);

        this.plowHandleGrip = new RendererModel(this, 50, 23);
        this.plowHandleGrip.addBox(-0.5F, 0.0F, -1.0F, 1, 5, 1);
        this.plowHandleGrip.setRotationPoint(0.0F, 32.8F, 21.0F);
        this.plowHandleGrip.rotateAngleX = (float) Math.PI / 4.0F;
        this.plowShaftUpper[1].addChild(this.plowHandleGrip);

        // --LEFT-WHEEL----------------------------------
        this.leftWheel = new RendererModel(this, 34, 4);
        this.leftWheel.setRotationPoint(14.5F, -11.0F, 1.0F);
        this.leftWheel.addBox(-2.0F, -1.0F, -1.0F, 2, 2, 2);
        for (int i = 0; i < 8; i++) {
            final RendererModel rim = new RendererModel(this, 8, 11);
            rim.addBox(-2.0F, -4.5F, 9.86F, 2, 9, 1);
            rim.rotateAngleX = i * (float) Math.PI / 4.0F;
            this.leftWheel.addChild(rim);

            final RendererModel spoke = new RendererModel(this, 14, 11);
            spoke.addBox(-1.5F, 1.0F, -0.5F, 1, 9, 1);
            spoke.rotateAngleX = i * (float) Math.PI / 4.0F;
            this.leftWheel.addChild(spoke);
        }

        // --RIGHT-WHEEL---------------------------------
        this.rightWheel = new RendererModel(this, 34, 4);
        this.rightWheel.mirror = true;
        this.rightWheel.setRotationPoint(-14.5F, -11.0F, 1.0F);
        this.rightWheel.addBox(0.0F, -1.0F, -1.0F, 2, 2, 2);
        for (int i = 0; i < 8; i++) {
            final RendererModel rim = new RendererModel(this, 8, 11);
            rim.addBox(0.0F, -4.5F, 9.86F, 2, 9, 1);
            rim.rotateAngleX = i * (float) Math.PI / 4.0F;
            this.rightWheel.addChild(rim);

            final RendererModel spoke = new RendererModel(this, 14, 11);
            spoke.addBox(0.5F, 1.0F, -0.5F, 1, 9, 1);
            spoke.rotateAngleX = i * (float) Math.PI / 4.0F;
            this.rightWheel.addChild(spoke);
        }

        this.parts = new RendererModel(this);
        this.parts.setRotationPoint(0.0F, -5.0F, -1.0F);
        this.parts.addChild(this.shafts);
        this.parts.addChild(this.triangle[0]);
        this.parts.addChild(this.triangle[1]);
        this.parts.addChild(this.triangle[2]);
        this.parts.addChild(this.plowShaftUpper[0]);
        this.parts.addChild(this.plowShaftUpper[1]);
        this.parts.addChild(this.plowShaftUpper[2]);
        this.body = new RendererModel(this);
        this.body.setRotationPoint(0.0F, -11.0F, 1.0F);
        this.body.addChild(this.axis);
        this.body.addChild(this.parts);
    }

    public RendererModel getBody() {
        return this.body;
    }

    public RendererModel getShaft(final int original) {
        return this.plowShaftLower[original];
    }

    @Override
    public void render(final PlowCartEntity entityIn, final float delta, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scale) {
        this.setRotationAngles(entityIn, delta, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        if (entityIn.getPlowing()) {
            for (final RendererModel renderer : this.plowShaftUpper) {
                renderer.rotateAngleX = (float) Math.PI / 4.0F;
            }
        } else {
            for (final RendererModel renderer : this.plowShaftUpper) {
                renderer.rotateAngleX = (float) Math.PI / 2.5F;
            }
        }
        this.leftWheel.render(scale);
        this.rightWheel.render(scale);
        this.body.render(scale);
    }

    @Override
    public void setRotationAngles(final PlowCartEntity entityIn, final float delta, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float pitch, final float scale) {
        this.body.rotateAngleX = (float) Math.toRadians(pitch);
        this.rightWheel.rotateAngleX = (float) (entityIn.getWheelRotation(0) + entityIn.getWheelRotationIncrement(0) * delta);
        this.leftWheel.rotateAngleX = (float) (entityIn.getWheelRotation(1) + entityIn.getWheelRotationIncrement(1) * delta);
    }
}
