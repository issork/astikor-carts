package de.mennomax.horsecarts.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelChariot extends ModelBase
{
    ModelRenderer base;
    ModelRenderer base2;
    ModelRenderer front;
    ModelRenderer rfront;
    ModelRenderer rfront2;
    ModelRenderer rfront3;
    ModelRenderer rbase;
    ModelRenderer rbase3;
    ModelRenderer rgrip;
    ModelRenderer rgrip2;
    ModelRenderer stick;

    public ModelChariot()
    {
        textureWidth = 128;
        textureHeight = 64;

        base = new ModelRenderer(this, 0, 0);
        base.addBox(0F, 0F, 0F, 22, 1, 16);
        base.setRotationPoint(-11F, 16F, -6F);
        base.setTextureSize(64, 32);
        base.mirror = true;
        setRotation(base, -0.1047198F, 0F, 0F);
        base2 = new ModelRenderer(this, 0, 0);
        base2.addBox(0F, 0F, -3F, 16, 1, 3);
        base2.setRotationPoint(-8F, 16F, -6F);
        base2.setTextureSize(64, 32);
        base2.mirror = true;
        setRotation(base2, -0.1047198F, 0F, 0F);
        front = new ModelRenderer(this, 0, 0);
        front.addBox(0F, -17F, 0F, 10, 20, 1);
        front.setRotationPoint(-5F, 14F, -10F);
        front.setTextureSize(64, 32);
        front.mirror = true;
        setRotation(front, 0.1745329F, 0F, 0F);
        rfront = new ModelRenderer(this, 0, 0);
        rfront.addBox(-6.4F, -15F, -0.1F, 6, 15, 1);
        rfront.setRotationPoint(-4F, 16F, -10F);
        rfront.setTextureSize(64, 32);
        rfront.mirror = true;
        setRotation(rfront, 0.1745329F, 0.2617994F, 0F);
        rfront3 = new ModelRenderer(this, 0, 0);
        rfront3.addBox(-5F, -12F, 0F, 5, 13, 1);
        rfront3.setRotationPoint(-10F, 15.5F, -8F);
        rfront3.setTextureSize(128, 64);
        rfront3.mirror = true;
        setRotation(rfront3, 0.2268928F, 0.9948377F, 0.1396263F);
        rbase = new ModelRenderer(this, 0, 0);
        rbase.addBox(0F, 0F, -2F, 2, 1, 2);
        rbase.setRotationPoint(-10F, 16F, -6F);
        rbase.setTextureSize(64, 32);
        rbase.mirror = true;
        setRotation(rbase, -0.1047198F, 0F, 0F);
        rbase3 = new ModelRenderer(this, 0, 0);
        rbase3.addBox(0F, 0F, 0F, 3, 1, 1);
        rbase3.setRotationPoint(-11F, 16F, -6F);
        rbase3.setTextureSize(64, 32);
        rbase3.mirror = true;
        setRotation(rbase3, -0.1047198F, 1.134464F, 0F);
        rgrip = new ModelRenderer(this, 0, 0);
        rgrip.addBox(0F, 0F, 0F, 1, 2, 14);
        rgrip.setRotationPoint(-13.6F, 4F, -7F);
        rgrip.setTextureSize(128, 64);
        rgrip.mirror = true;
        setRotation(rgrip, -0.3316126F, 0.0174533F, -0.5061455F);
        rgrip2 = new ModelRenderer(this, 0, 0);
        rgrip2.addBox(0F, 0F, -2F, 1, 10, 2);
        rgrip2.setRotationPoint(-11F, 8F, 6F);
        rgrip2.setTextureSize(128, 64);
        rgrip2.mirror = true;
        setRotation(rgrip2, 0.0698132F, 0.0523599F, 0F);
        stick = new ModelRenderer(this, 0, 0);
        stick.addBox(-0.5F, 0F, -31F, 1, 1, 31);
        stick.setRotationPoint(0.5F, 16F, -9F);
        stick.setTextureSize(128, 64);
        stick.mirror = true;
        setRotation(stick, -0.2792527F, 0F, 0F);
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        base.render(f5);
        base2.render(f5);
        front.render(f5);
        rfront.render(f5);
        rfront3.render(f5);
        rbase.render(f5);
        rbase3.render(f5);
        rgrip.render(f5);
        rgrip2.render(f5);
        stick.render(f5);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity)
    {
        super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
    }

}
