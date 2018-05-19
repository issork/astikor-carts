package astikoor.client.model;

import astikoor.entity.EntityCargoCart;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelCargoCart extends ModelBase
{
    private ModelRenderer boardBottom;
    private ModelRenderer axis;
    private ModelRenderer shaft;
    private ModelRenderer boardFront;
    private ModelRenderer[] boardsSide = new ModelRenderer[4];
    private ModelRenderer[] boardsRear = new ModelRenderer[2];
    private ModelRenderer[] cargo = new ModelRenderer[4];
    private ModelRenderer leftWheel;
    private ModelRenderer rightWheel;

    public ModelCargoCart()
    {
        // --BOTTOM-BOARD--------------------------------------
        this.boardBottom = (new ModelRenderer(this, 0, 0)).setTextureSize(128, 64);
        this.boardBottom.addBox(-14.5F, -11.0F, 3.0F, 29, 22, 1);
        this.boardBottom.rotateAngleY = ((float) Math.PI / -2F);
        this.boardBottom.rotateAngleX = ((float) Math.PI / -2F);

        // --AXIS--------------------------------------
        this.axis = (new ModelRenderer(this, 0, 23)).setTextureSize(128, 64);
        this.axis.addBox(-12.5F, 4.0F, 0.0F, 25, 2, 2);

        // --SHAFTS--------------------------------------
        this.shaft = (new ModelRenderer(this, 0, 35)).setTextureSize(128, 64);
        this.shaft.setRotationPoint(0.0F, 0.0F, -14.0F);
        this.shaft.rotateAngleY = ((float) Math.PI / 2.0F);
        this.shaft.rotateAngleZ = -0.07F;
        this.shaft.addBox(0.0F, 0.0F, -8.0F, 20, 2, 1);
        this.shaft.addBox(0.0F, 0.0F, 7.0F, 20, 2, 1);

        // --FRONT-BOARD---------------------------------
        this.boardFront = (new ModelRenderer(this, 0, 38)).setTextureSize(128, 64);
        this.boardFront.addBox(-12.0F, -7.0F, -14.5F, 24, 10, 1);

        // --BOARDS-SIDE---------------------------------
        this.boardsSide[0] = (new ModelRenderer(this, 0, 27)).setTextureSize(128, 64);
        this.boardsSide[0].addBox(-13.5F, -7.0F, 0.0F, 28, 3, 1);
        this.boardsSide[0].setRotationPoint(-11.0F, 0.0F, 0.0F);
        this.boardsSide[0].rotateAngleY = ((float) Math.PI / -2.0F);
        this.boardsSide[0].mirror = true;

        this.boardsSide[1] = (new ModelRenderer(this, 0, 27)).setTextureSize(128, 64);
        this.boardsSide[1].addBox(-14.5F, -7.0F, 0.0F, 28, 3, 1);
        this.boardsSide[1].setRotationPoint(11.0F, 0.0F, 0.0F);
        this.boardsSide[1].rotateAngleY = ((float) Math.PI / 2.0F);

        this.boardsSide[2] = (new ModelRenderer(this, 0, 31)).setTextureSize(128, 64);
        this.boardsSide[2].addBox(-13.5F, -2.0F, 0.0F, 28, 3, 1);
        this.boardsSide[2].setRotationPoint(-11.0F, 0.0F, 0.0F);
        this.boardsSide[2].rotateAngleY = ((float) Math.PI / -2.0F);
        this.boardsSide[2].mirror = true;

        this.boardsSide[3] = (new ModelRenderer(this, 0, 31)).setTextureSize(128, 64);
        this.boardsSide[3].addBox(-14.5F, -2.0F, 0.0F, 28, 3, 1);
        this.boardsSide[3].setRotationPoint(11.0F, 0.0F, 0.0F);
        this.boardsSide[3].rotateAngleY = ((float) Math.PI / 2.0F);

        // --REAR-BOARDS---------------------------------
        this.boardsRear[0] = (new ModelRenderer(this, 50, 35)).setTextureSize(128, 64);
        this.boardsRear[0].addBox(10.0F, -7.0F, 14.5F, 2, 11, 1);

        this.boardsRear[1] = (new ModelRenderer(this, 50, 35)).setTextureSize(128, 64);
        this.boardsRear[1].addBox(-12.0F, -7.0F, 14.5F, 2, 11, 1);
        this.boardsRear[1].mirror = true;

        // --CARGO---------------------------------------
        this.cargo[0] = (new ModelRenderer(this, 64, 38)).setTextureSize(128, 64);
        this.cargo[0].addBox(-9.5F, -5.0F, -12.5F, 8, 8, 8);
        this.cargo[0].rotateAngleY = 0.05F;

        this.cargo[1] = (new ModelRenderer(this, 64, 18)).setTextureSize(128, 64);
        this.cargo[1].addBox(-1.0F, -7.0F, -12.5F, 11, 10, 10);

        this.cargo[2] = (new ModelRenderer(this, 64, 0)).setTextureSize(128, 64);
        this.cargo[2].addBox(-10.5F, -5.0F, -8.5F, 20, 8, 10);
        this.cargo[2].rotateAngleY = (float) Math.PI;

        this.cargo[3] = (new ModelRenderer(this, 64, 54)).setTextureSize(128, 64);
        this.cargo[3].addBox(-12.0F, -7.0F, 1.0F, 20, 2, 3);
        this.cargo[3].rotateAngleY = -1.067F;

        // --LEFT-WHEEL----------------------------------
        this.leftWheel = (new ModelRenderer(this, 54, 23)).setTextureSize(128, 64);
        this.leftWheel.setRotationPoint(14.5F, 5.0F, 1.0F);
        this.leftWheel.addBox(-2.0F, -1.0F, -1.0F, 1, 2, 2);
        for(int i = 0; i < 8; i++)
        {
            ModelRenderer rim = (new ModelRenderer(this, 60, 0)).setTextureSize(128, 64);
            rim.addBox(-1.5F, -4.5F, 9.86F, 1, 9, 1);
            rim.rotateAngleX = i * ((float) Math.PI / 4.0F);
            this.leftWheel.addChild(rim);

            ModelRenderer spoke = (new ModelRenderer(this, 60, 10)).setTextureSize(128, 64);
            spoke.addBox(-1.5F, 1.0F, -0.5F, 1, 9, 1);
            spoke.rotateAngleX = i * ((float) Math.PI / 4.0F);
            this.leftWheel.addChild(spoke);
        }

        // --RIGHT-WHEEL---------------------------------
        this.rightWheel = (new ModelRenderer(this, 0, 42)).setTextureSize(128, 128);
        this.rightWheel.mirror = true;
        this.rightWheel.setRotationPoint(-14.5F, 5.0F, 1.0F);
        this.rightWheel.addBox(1.0F, -1.0F, -1.0F, 1, 2, 2);
        for(int i = 0; i < 8; i++)
        {
            ModelRenderer rim = (new ModelRenderer(this, 60, 0)).setTextureSize(128, 64);
            rim.addBox(0.5F, -4.5F, 9.86F, 1, 9, 1);
            rim.rotateAngleX = i * ((float) Math.PI / 4.0F);
            this.rightWheel.addChild(rim);

            ModelRenderer spoke = (new ModelRenderer(this, 60, 10)).setTextureSize(128, 64);
            spoke.addBox(0.5F, 1.0F, -0.5F, 1, 9, 1);
            spoke.rotateAngleX = i * ((float) Math.PI / 4.0F);
            this.rightWheel.addChild(spoke);
        }
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch, float scale)
    {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, rotationYaw, rotationPitch, scale, entity);
        this.axis.render(scale);
        this.shaft.renderWithRotation(scale);
        this.boardBottom.render(scale);
        this.boardFront.render(scale);
        for(int i = 0; i < 2; ++i)
        {
            this.boardsRear[i].render(scale);
        }
        for(int i = 0; i < 4; ++i)
        {
            this.boardsSide[i].render(scale);
        }
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch, float scale, Entity entity)
    {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, rotationYaw, rotationPitch, scale, entity);
        this.leftWheel.rotateAngleX = ((EntityCargoCart) entity).getWheelRotation();
        this.rightWheel.rotateAngleX = ((EntityCargoCart) entity).getWheelRotation();

        this.leftWheel.render(scale);
        this.rightWheel.render(scale);

        for(int i = 0; i < ((EntityCargoCart) entity).load; i++)
        {
            this.cargo[i].render(scale);
        }
    }
}
