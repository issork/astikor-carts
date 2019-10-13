package de.mennomax.astikorcarts.client.renderer.entity.model;

import de.mennomax.astikorcarts.entity.MobCartEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MobCartModel extends EntityModel<MobCartEntity> {
    private RendererModel axis;
    private RendererModel cartBase;
    private RendererModel shaft;
    private RendererModel boardLeft;
    private RendererModel boardRight;
    private RendererModel boardBack;
    private RendererModel boardFront;
    private RendererModel leftWheel;
    private RendererModel rightWheel;

    public MobCartModel() {
        this.textureWidth = 64;
        this.textureHeight = 64;

        this.axis = new RendererModel(this, 0, 21);
        this.axis.addBox(-12.5F, 4.0F, 0.0F, 25, 2, 2);

        this.cartBase = new RendererModel(this, 0, 0);
        this.cartBase.addBox(-14.5F, -10.0F, 3.0F, 29, 20, 1);
        this.cartBase.rotateAngleX = -1.570796F;
        this.cartBase.rotateAngleY = -1.570796F;

        this.shaft = new RendererModel(this, 0, 25);
        this.shaft.setRotationPoint(0.0F, 0.0F, -14.0F);
        this.shaft.rotateAngleY = (float) Math.PI / 2.0F;
        this.shaft.addBox(0.0F, -0.5F, -8.0F, 20, 2, 1);
        this.shaft.addBox(0.0F, -0.5F, 7.0F, 20, 2, 1);

        this.boardLeft = new RendererModel(this, 0, 28);
        this.boardLeft.addBox(-5F, -15.5F, 9F, 8, 31, 2);
        this.boardLeft.rotateAngleX = -1.570796F;
        this.boardLeft.rotateAngleZ = 1.570796F;

        this.boardRight = new RendererModel(this, 0, 28);
        this.boardRight.addBox(-5F, -15.5F, -11F, 8, 31, 2);
        this.boardRight.rotateAngleX = -1.570796F;
        this.boardRight.rotateAngleZ = 1.570796F;

        this.boardBack = new RendererModel(this, 20, 28);
        this.boardBack.addBox(-9F, -5F, 13.5F, 18, 8, 2);

        this.boardFront = new RendererModel(this, 20, 28);
        this.boardFront.addBox(-9F, -5F, -15.5F, 18, 8, 2);

        // --LEFT-WHEEL----------------------------------
        this.leftWheel = new RendererModel(this, 52, 21);
        this.leftWheel.setRotationPoint(14.5F, 5.0F, 1.0F);
        this.leftWheel.addBox(-2.0F, -1.0F, -1.0F, 2, 2, 2);
        for (int i = 0; i < 8; i++) {
            RendererModel rim = new RendererModel(this, 20, 38);
            rim.addBox(-2.0F, -4.5F, 9.86F, 2, 9, 1);
            rim.rotateAngleX = i * (float) Math.PI / 4.0F;
            this.leftWheel.addChild(rim);

            RendererModel spoke = new RendererModel(this, 20, 48);
            spoke.addBox(-1.5F, 1.0F, -0.5F, 1, 9, 1);
            spoke.rotateAngleX = i * (float) Math.PI / 4.0F;
            this.leftWheel.addChild(spoke);
        }

        // --RIGHT-WHEEL---------------------------------
        this.rightWheel = new RendererModel(this, 52, 21);
        this.rightWheel.mirror = true;
        this.rightWheel.setRotationPoint(-14.5F, 5.0F, 1.0F);
        this.rightWheel.addBox(0.0F, -1.0F, -1.0F, 2, 2, 2);
        for (int i = 0; i < 8; i++) {
            RendererModel rim = new RendererModel(this, 20, 38);
            rim.addBox(0.0F, -4.5F, 9.86F, 2, 9, 1);
            rim.rotateAngleX = i * (float) Math.PI / 4.0F;
            this.rightWheel.addChild(rim);

            RendererModel spoke = new RendererModel(this, 20, 48);
            spoke.addBox(0.5F, 1.0F, -0.5F, 1, 9, 1);
            spoke.rotateAngleX = i * (float) Math.PI / 4.0F;
            this.rightWheel.addChild(spoke);
        }

    }

    @Override
    public void render(MobCartEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        this.leftWheel.render(scale);
        this.rightWheel.render(scale);
        this.axis.render(scale);
        this.cartBase.render(scale);
        this.shaft.render(scale);
        this.boardLeft.render(scale);
        this.boardRight.render(scale);
        this.boardBack.render(scale);
        this.boardFront.render(scale);
    }

    @Override
    public void setRotationAngles(MobCartEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.rightWheel.rotateAngleX = (float) (entityIn.getWheelRotation(0) + entityIn.getWheelRotationIncrement(0) * limbSwing);
        this.leftWheel.rotateAngleX = (float) (entityIn.getWheelRotation(1) + entityIn.getWheelRotationIncrement(1) * limbSwing);
    }
}
