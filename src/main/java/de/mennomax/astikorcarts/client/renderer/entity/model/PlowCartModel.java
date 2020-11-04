package de.mennomax.astikorcarts.client.renderer.entity.model;

import de.mennomax.astikorcarts.entity.PlowCartEntity;
import net.minecraft.client.renderer.model.ModelRenderer;

public final class PlowCartModel extends CartModel<PlowCartEntity> {
    private final ModelRenderer axis;
    private final ModelRenderer[] triangle = new ModelRenderer[3];
    private final ModelRenderer shaft;
    private final ModelRenderer shaftConnector;
    private final ModelRenderer[] plowShaftUpper = new ModelRenderer[3];
    private final ModelRenderer[] plowShaftLower = new ModelRenderer[3];
    private final ModelRenderer plowHandle;
    private final ModelRenderer plowHandleGrip;
    private final ModelRenderer parts;
    private final ModelRenderer shafts;

    public PlowCartModel() {
        super(64, 64);

        this.axis = new ModelRenderer(this, 0, 0);
        this.axis.addBox(-12.5F, -1.0F, -1.0F, 25, 2, 2);

        this.triangle[0] = new ModelRenderer(this, 0, 4);
        this.triangle[0].addBox(-7.5F, -9.0F, 0.0F, 15, 2, 2);

        this.triangle[1] = new ModelRenderer(this, 0, 11);
        this.triangle[1].addBox(-5.0F, -9.0F, 0.5F, 2, 14, 2);
        this.triangle[1].rotateAngleZ = -0.175F;

        this.triangle[2] = new ModelRenderer(this, 0, 11);
        this.triangle[2].addBox(3.0F, -9.0F, 0.5F, 2, 14, 2);
        this.triangle[2].rotateAngleZ = 0.175F;
        this.triangle[2].mirror = true;

        this.shaft = new ModelRenderer(this, 0, 8);
        this.shaft.rotateAngleZ = -0.07F;
        this.shaft.addBox(0.0F, 0.0F, -8.0F, 20, 2, 1);
        this.shaft.addBox(0.0F, 0.0F, 7.0F, 20, 2, 1);

        this.shaftConnector = new ModelRenderer(this, 0, 27);
        this.shaftConnector.rotateAngleZ = -0.26F;
        this.shaftConnector.addBox(-16.0F, 0.0F, -8.0F, 16, 2, 1);
        this.shaftConnector.addBox(-16.0F, 0.0F, 7.0F, 16, 2, 1);

        this.shafts = new ModelRenderer(this);
        this.shafts.setRotationPoint(0.0F, 0.0F, -14.0F);
        this.shafts.rotateAngleY = (float) Math.PI / 2.0F;
        this.shafts.addChild(this.shaft);
        this.shafts.addChild(this.shaftConnector);

        for (int i = 0; i < this.plowShaftUpper.length; i++) {
            this.plowShaftUpper[i] = new ModelRenderer(this, 56, 0);
            this.plowShaftUpper[i].addBox(-1.0F, -2.0F, -2.0F, 2, 30, 2);
            this.plowShaftUpper[i].setRotationPoint(-3.0F + 3 * i, -7.0F, 0.0F);
            this.plowShaftUpper[i].rotateAngleY = -0.523599F + (float) Math.PI / 6.0F * i;

            this.plowShaftLower[i] = new ModelRenderer(this, 42, 4);
            this.plowShaftLower[i].addBox(-1.0F, -0.7F, -0.7F, 2, 10, 2);
            this.plowShaftLower[i].setRotationPoint(0.0F, 28.0F, -1.0F);
            this.plowShaftLower[i].rotateAngleX = (float) Math.PI / 4.0F;
            this.plowShaftUpper[i].addChild(this.plowShaftLower[i]);
        }

        this.plowHandle = new ModelRenderer(this, 50, 4);
        this.plowHandle.addBox(-0.5F, 0.0F, -0.5F, 1, 18, 1);
        this.plowHandle.setRotationPoint(0.0F, 33.0F, 5.0F);
        this.plowHandle.rotateAngleX = (float) Math.PI / 2.0F;
        this.plowShaftUpper[1].addChild(this.plowHandle);

        this.plowHandleGrip = new ModelRenderer(this, 50, 23);
        this.plowHandleGrip.addBox(-0.5F, 0.0F, -1.0F, 1, 5, 1);
        this.plowHandleGrip.setRotationPoint(0.0F, 32.8F, 21.0F);
        this.plowHandleGrip.rotateAngleX = (float) Math.PI / 4.0F;
        this.plowShaftUpper[1].addChild(this.plowHandleGrip);

        this.parts = new ModelRenderer(this);
        this.parts.setRotationPoint(0.0F, -5.0F, -1.0F);
        this.parts.addChild(this.shafts);
        this.parts.addChild(this.triangle[0]);
        this.parts.addChild(this.triangle[1]);
        this.parts.addChild(this.triangle[2]);
        this.parts.addChild(this.plowShaftUpper[0]);
        this.parts.addChild(this.plowShaftUpper[1]);
        this.parts.addChild(this.plowShaftUpper[2]);
        this.body.addChild(this.axis);
        this.body.addChild(this.parts);
    }

    public ModelRenderer getBody() {
        return this.body;
    }

    public ModelRenderer getShaft(final int original) {
        return this.plowShaftLower[original];
    }


    @Override
    public void setRotationAngles(final PlowCartEntity entity, final float delta, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float pitch) {
        super.setRotationAngles(entity, delta, limbSwingAmount, ageInTicks, netHeadYaw, pitch);
        for (final ModelRenderer renderer : this.plowShaftUpper) {
            renderer.rotateAngleX = (float) (entity.getPlowing() ? Math.PI / 4.0D - Math.toRadians(pitch) : Math.PI / 2.5D);
        }
    }
}
