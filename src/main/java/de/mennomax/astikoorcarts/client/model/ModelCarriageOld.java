package de.mennomax.astikoorcarts.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelCarriageOld extends ModelBase
{
    private ModelRenderer bottomBoard;
    private ModelRenderer[] sideBoards = new ModelRenderer[6];

    public ModelCarriageOld()
    {
        // --BOTTOM-BOARD--------------------------------------
        this.bottomBoard = (new ModelRenderer(this, 0, 0)).setTextureSize(128, 64);
        this.bottomBoard.addBox(-21.5F, -17.0F, 3.0F, 55, 34, 1);
        this.bottomBoard.rotateAngleY = ((float) Math.PI / -2F);
        this.bottomBoard.rotateAngleX = ((float) Math.PI / -2F);

        // --SIDE-BOARDS----------------------------------------
        this.sideBoards[0] = (new ModelRenderer(this, 0, 0)).setTextureSize(128, 64);
        this.sideBoards[0].addBox(-18.0F, -11.5F, 2.0F, 44, 1, 4);
        this.sideBoards[0].setRotationPoint(-11.5F, 0.0F, 0.0F);
        this.sideBoards[0].rotateAngleY = ((float) Math.PI / -2F);
    }

    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch, float scale)
    {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, rotationYaw, rotationPitch, scale, entity);
        this.bottomBoard.render(scale);
        this.sideBoards[0].render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch, float scale, Entity entity)
    {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, rotationYaw, rotationPitch, scale, entity);
    }
}
