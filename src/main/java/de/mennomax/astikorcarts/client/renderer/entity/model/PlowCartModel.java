package de.mennomax.astikorcarts.client.renderer.entity.model;

import de.mennomax.astikorcarts.entity.PlowCartEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.RendererModel;

public class PlowCartModel extends EntityModel<PlowCartEntity>
{
    private RendererModel axis;
    private RendererModel[] triangle = new RendererModel[3];
    private RendererModel shaft;
    private RendererModel shaftConnector;
    private RendererModel[] plowShaftUpper = new RendererModel[3];
    private RendererModel[] plowShaftLower = new RendererModel[3];
    private RendererModel plowHandle;
    private RendererModel plowHandleGrip;
    private RendererModel leftWheel;
    private RendererModel rightWheel;

    public PlowCartModel()
    {
        // --AXIS--------------------------------------
        this.axis = new RendererModel(this, 0, 0);
        this.axis.addBox(-12.5F, 4.0F, 0.0F, 25, 2, 2);

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
        this.shaft.setRotationPoint(0.0F, 0.0F, -14.0F);
        this.shaft.rotateAngleY = (float) Math.PI / 2.0F;
        this.shaft.rotateAngleZ = -0.07F;
        this.shaft.addBox(0.0F, 0.0F, -8.0F, 20, 2, 1);
        this.shaft.addBox(0.0F, 0.0F, 7.0F, 20, 2, 1);

        this.shaftConnector = new RendererModel(this, 0, 27);
        this.shaftConnector.setRotationPoint(0.0F, 0.0F, -14.0F);
        this.shaftConnector.rotateAngleY = (float) Math.PI / 2.0F;
        this.shaftConnector.rotateAngleZ = -0.26F;
        this.shaftConnector.addBox(-16.0F, 0.0F, -8.0F, 16, 2, 1);
        this.shaftConnector.addBox(-16.0F, 0.0F, 7.0F, 16, 2, 1);

        // --PLOW-SHAFT---------------------------------
        for(int i = 0; i < this.plowShaftUpper.length; i++)
        {
            this.plowShaftUpper[i] = new RendererModel(this, 56, 0);
            this.plowShaftUpper[i].addBox(-1.0F, -2.0F, -2.0F, 2, 30, 2);
            this.plowShaftUpper[i].setRotationPoint(-3.0F+3*i, -7.0F, 0.0F);
            this.plowShaftUpper[i].rotateAngleY = -0.523599F + (float) Math.PI / 6.0F * i;
            
            this.plowShaftLower[i] = new RendererModel(this, 42, 4);
            this.plowShaftLower[i].addBox(-1.0F, -0.7F, -0.7F, 2, 10, 2);
            this.plowShaftLower[i].setRotationPoint(0.0F, 28.0F, -1.0F);
            this.plowShaftLower[i].rotateAngleX = (float) Math.PI / 4.0F;
            this.plowShaftUpper[i].addChild(plowShaftLower[i]);
        }

        this.plowHandle = new RendererModel(this, 50, 4);
        this.plowHandle.addBox(-0.5F, 0.0F, -0.5F, 1, 18, 1);
        this.plowHandle.setRotationPoint(0.0F, 33.0F, 5.0F);
        this.plowHandle.rotateAngleX = (float) Math.PI / 2.0F;
        this.plowShaftUpper[1].addChild(plowHandle);

        this.plowHandleGrip = new RendererModel(this, 50, 23);
        this.plowHandleGrip.addBox(-0.5F, 0.0F, -1.0F, 1, 5, 1);
        this.plowHandleGrip.setRotationPoint(0.0F, 32.8F, 21.0F);
        this.plowHandleGrip.rotateAngleX = (float) Math.PI / 4.0F;
        this.plowShaftUpper[1].addChild(plowHandleGrip);

        // --LEFT-WHEEL----------------------------------
        this.leftWheel = new RendererModel(this, 34, 4);
        this.leftWheel.setRotationPoint(14.5F, 5.0F, 1.0F);
        this.leftWheel.addBox(-2.0F, -1.0F, -1.0F, 2, 2, 2);
        for (int i = 0; i < 8; i++)
        {
            RendererModel rim = new RendererModel(this, 8, 11);
            rim.addBox(-2.0F, -4.5F, 9.86F, 2, 9, 1);
            rim.rotateAngleX = i * (float) Math.PI / 4.0F;
            this.leftWheel.addChild(rim);

            RendererModel spoke = new RendererModel(this, 14, 11);
            spoke.addBox(-1.5F, 1.0F, -0.5F, 1, 9, 1);
            spoke.rotateAngleX = i * (float) Math.PI / 4.0F;
            this.leftWheel.addChild(spoke);
        }

        // --RIGHT-WHEEL---------------------------------
        this.rightWheel = new RendererModel(this, 34, 4);
        this.rightWheel.mirror = true;
        this.rightWheel.setRotationPoint(-14.5F, 5.0F, 1.0F);
        this.rightWheel.addBox(0.0F, -1.0F, -1.0F, 2, 2, 2);
        for (int i = 0; i < 8; i++)
        {
            RendererModel rim = new RendererModel(this, 8, 11);
            rim.addBox(0.0F, -4.5F, 9.86F, 2, 9, 1);
            rim.rotateAngleX = i * (float) Math.PI / 4.0F;
            this.rightWheel.addChild(rim);

            RendererModel spoke = new RendererModel(this, 14, 11);
            spoke.addBox(0.5F, 1.0F, -0.5F, 1, 9, 1);
            spoke.rotateAngleX = i * (float) Math.PI / 4.0F;
            this.rightWheel.addChild(spoke);
        }
    }

    @Override
    public void render(PlowCartEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        this.axis.render(scale);
        this.shaft.renderWithRotation(scale);
        this.shaftConnector.renderWithRotation(scale);
        for (int i = 0; i < 3; ++i)
        {
            this.triangle[i].render(scale);
        }
    }

    @Override
    public void setRotationAngles(PlowCartEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
//        this.leftWheel.rotateAngleX = ((PlowCartEntity) entity).getWheelRotation();
//        this.rightWheel.rotateAngleX = this.leftWheel.rotateAngleX;

        this.leftWheel.render(scale);
        this.rightWheel.render(scale);

        if (entityIn.getPlowing())
        {
            for(RendererModel renderer : this.plowShaftUpper)
            {
                renderer.rotateAngleX = (float) Math.PI / 4.0F;
            }
        }
        else
        {
            for(RendererModel renderer : this.plowShaftUpper)
            {
                renderer.rotateAngleX = (float) Math.PI / 2.5F;
            }
        }
        for(RendererModel renderer : this.plowShaftUpper)
        {
            renderer.render(scale);
        }
    }
}
