package de.mennomax.astikorcarts.client.renderer.entity.model;

import de.mennomax.astikorcarts.entity.CargoCartEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CargoCartModel extends EntityModel<CargoCartEntity>
{
    private RendererModel boardBottom;
    private RendererModel axis;
    private RendererModel shaft;
    private RendererModel boardFront;
    private RendererModel[] boardsSide = new RendererModel[4];
    private RendererModel[] boardsRear = new RendererModel[2];
    private RendererModel[] cargo = new RendererModel[4];
    private RendererModel leftWheel;
    private RendererModel rightWheel;

    public CargoCartModel()
    {
        this.textureWidth = 128;
        this.textureHeight = 64;
        
        // --BOTTOM-BOARD--------------------------------------
        this.boardBottom = new RendererModel(this, 0, 0);
        this.boardBottom.addBox(-14.5F, -11.0F, 3.0F, 29, 22, 1);
        this.boardBottom.rotateAngleY = (float) Math.PI / -2F;
        this.boardBottom.rotateAngleX = (float) Math.PI / -2F;

        // --AXIS--------------------------------------
        this.axis = new RendererModel(this, 0, 23);
        this.axis.addBox(-12.5F, 4.0F, 0.0F, 25, 2, 2);

        // --SHAFTS--------------------------------------
        this.shaft = new RendererModel(this, 0, 35);
        this.shaft.setRotationPoint(0.0F, 0.0F, -14.0F);
        this.shaft.rotateAngleY = (float) Math.PI / 2.0F;
        this.shaft.addBox(0.0F, -2.5F, -8.0F, 20, 2, 1);
        this.shaft.addBox(0.0F, -2.5F, 7.0F, 20, 2, 1);

        // --FRONT-BOARD---------------------------------
        this.boardFront = new RendererModel(this, 0, 38);
        this.boardFront.addBox(-12.0F, -7.0F, -14.5F, 24, 10, 1);

        // --BOARDS-SIDE---------------------------------
        this.boardsSide[0] = new RendererModel(this, 0, 27);
        this.boardsSide[0].addBox(-13.5F, -7.0F, 0.0F, 28, 3, 1);
        this.boardsSide[0].setRotationPoint(-11.0F, 0.0F, 0.0F);
        this.boardsSide[0].rotateAngleY = (float) Math.PI / -2.0F;

        this.boardsSide[1] = new RendererModel(this, 0, 27);
        this.boardsSide[1].addBox(-14.5F, -7.0F, 0.0F, 28, 3, 1);
        this.boardsSide[1].setRotationPoint(11.0F, 0.0F, 0.0F);
        this.boardsSide[1].rotateAngleY = (float) Math.PI / 2.0F;

        this.boardsSide[2] = new RendererModel(this, 0, 31);
        this.boardsSide[2].addBox(-13.5F, -2.0F, 0.0F, 28, 3, 1);
        this.boardsSide[2].setRotationPoint(-11.0F, 0.0F, 0.0F);
        this.boardsSide[2].rotateAngleY = (float) Math.PI / -2.0F;

        this.boardsSide[3] = new RendererModel(this, 0, 31);
        this.boardsSide[3].addBox(-14.5F, -2.0F, 0.0F, 28, 3, 1);
        this.boardsSide[3].setRotationPoint(11.0F, 0.0F, 0.0F);
        this.boardsSide[3].rotateAngleY = (float) Math.PI / 2.0F;

        // --REAR-BOARDS---------------------------------
        this.boardsRear[0] = new RendererModel(this, 50, 35);
        this.boardsRear[0].addBox(10.0F, -7.0F, 14.5F, 2, 11, 1);

        this.boardsRear[1] = new RendererModel(this, 50, 35);
        this.boardsRear[1].addBox(-12.0F, -7.0F, 14.5F, 2, 11, 1);

        // --CARGO---------------------------------------
        this.cargo[0] = new RendererModel(this, 66, 38);
        this.cargo[0].addBox(-9.5F, -5.0F, -12.5F, 8, 8, 8);
        this.cargo[0].rotateAngleY = 0.05F;

        this.cargo[1] = new RendererModel(this, 66, 18);
        this.cargo[1].addBox(-1.0F, -7.0F, -12.5F, 11, 10, 10);

        this.cargo[2] = new RendererModel(this, 66, 0);
        this.cargo[2].addBox(-10.5F, -5.0F, -8.5F, 20, 8, 10);
        this.cargo[2].rotateAngleY = (float) Math.PI;

        this.cargo[3] = new RendererModel(this, 66, 54);
        this.cargo[3].addBox(-12.0F, -7.0F, 1.0F, 20, 2, 3);
        this.cargo[3].rotateAngleY = -1.067F;

        // --RIGHT-WHEEL----------------------------------
        this.leftWheel = new RendererModel(this, 54, 23);
        this.leftWheel.setRotationPoint(14.5F, 5.0F, 1.0F);
        this.leftWheel.addBox(-2.0F, -1.0F, -1.0F, 2, 2, 2);
        for (int i = 0; i < 8; i++)
        {
            RendererModel rim = new RendererModel(this, 60, 0);
            rim.addBox(-2.0F, -4.5F, 9.86F, 2, 9, 1);
            rim.rotateAngleX = i * (float) Math.PI / 4.0F;
            this.leftWheel.addChild(rim);

            RendererModel spoke = new RendererModel(this, 60, 10);
            spoke.addBox(-1.5F, 1.0F, -0.5F, 1, 9, 1);
            spoke.rotateAngleX = i * (float) Math.PI / 4.0F;
            this.leftWheel.addChild(spoke);
        }

        // --LEFT-WHEEL---------------------------------
        this.rightWheel = new RendererModel(this, 54, 23);
        this.rightWheel.setRotationPoint(-14.5F, 5.0F, 1.0F);
        this.rightWheel.addBox(0.0F, -1.0F, -1.0F, 2, 2, 2);
        for (int i = 0; i < 8; i++)
        {
            RendererModel rim = new RendererModel(this, 60, 0);
            rim.addBox(0.0F, -4.5F, 9.86F, 2, 9, 1);
            rim.rotateAngleX = i * (float) Math.PI / 4.0F;
            this.rightWheel.addChild(rim);

            RendererModel spoke = new RendererModel(this, 60, 10);
            spoke.addBox(0.5F, 1.0F, -0.5F, 1, 9, 1);
            spoke.rotateAngleX = i * (float) Math.PI / 4.0F;
            this.rightWheel.addChild(spoke);
        }
    }

    @Override
    public void render(CargoCartEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        this.axis.render(scale);
        this.shaft.renderWithRotation(scale);
        this.boardBottom.render(scale);
        this.boardFront.render(scale);
        for (int i = 0; i < 2; ++i)
        {
            this.boardsRear[i].render(scale);
        }
        for (int i = 0; i < 4; ++i)
        {
            this.boardsSide[i].render(scale);
        }
    }

    @Override
    public void setRotationAngles(CargoCartEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
//        this.leftWheel.rotateAngleX = entityIn.getWheelRotation();
//        this.rightWheel.rotateAngleX = this.leftWheel.rotateAngleX;
//        this.leftWheel.rotateAngleX = this.leftWheel.rotateAngleX+0.1F*limbSwing;
        rightWheel.rotateAngleX = leftWheel.rotateAngleX;
        this.leftWheel.render(scale);
        this.rightWheel.render(scale);

        for (int i = 0; i < entityIn.getCargo(); i++)
        {
            this.cargo[i].render(scale);
        }
    }
}
