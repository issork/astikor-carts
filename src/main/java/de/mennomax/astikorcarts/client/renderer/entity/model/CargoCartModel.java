package de.mennomax.astikorcarts.client.renderer.entity.model;

import de.mennomax.astikorcarts.entity.CargoCartEntity;
import net.minecraft.client.renderer.model.ModelRenderer;

public final class CargoCartModel extends CartModel<CargoCartEntity> {
    private final ModelRenderer boardBottom;
    private final ModelRenderer axis;
    private final ModelRenderer shaft;
    private final ModelRenderer boardFront;
    private final ModelRenderer[] boardsSide = new ModelRenderer[4];
    private final ModelRenderer[] boardsRear = new ModelRenderer[2];

    public CargoCartModel() {
        super(64, 64);

        this.boardBottom = new ModelRenderer(this, 0, 0);
        this.boardBottom.addBox(-15.5F, -11.0F, -2.0F, 29, 22, 1);
        this.boardBottom.rotateAngleY = (float) Math.PI / -2.0F;
        this.boardBottom.rotateAngleX = (float) Math.PI / -2.0F;

        this.axis = new ModelRenderer(this, 0, 23);
        this.axis.addBox(-12.5F, -1.0F, -1.0F, 25, 2, 2);

        this.shaft = new ModelRenderer(this, 0, 35);
        this.shaft.setRotationPoint(0.0F, -5.0F, -15.0F);
        this.shaft.rotateAngleY = (float) Math.PI / 2.0F;
        this.shaft.addBox(0.0F, -2.5F, -8.0F, 20, 2, 1);
        this.shaft.addBox(0.0F, -2.5F, 7.0F, 20, 2, 1);

        this.boardFront = new ModelRenderer(this, 0, 38);
        this.boardFront.addBox(-12.0F, -12.0F, -15.5F, 24, 10, 1);

        this.boardsSide[0] = new ModelRenderer(this, 0, 27);
        this.boardsSide[0].addBox(-13.5F, -7.0F, 0.0F, 28, 3, 1);
        this.boardsSide[0].setRotationPoint(-11.0F, -5.0F, -1.0F);
        this.boardsSide[0].rotateAngleY = (float) Math.PI / -2.0F;

        this.boardsSide[1] = new ModelRenderer(this, 0, 27);
        this.boardsSide[1].addBox(-14.5F, -7.0F, 0.0F, 28, 3, 1);
        this.boardsSide[1].setRotationPoint(11.0F, -5.0F, -1.0F);
        this.boardsSide[1].rotateAngleY = (float) Math.PI / 2.0F;

        this.boardsSide[2] = new ModelRenderer(this, 0, 31);
        this.boardsSide[2].addBox(-13.5F, -2.0F, 0.0F, 28, 3, 1);
        this.boardsSide[2].setRotationPoint(-11.0F, -5.0F, -1.0F);
        this.boardsSide[2].rotateAngleY = (float) Math.PI / -2.0F;

        this.boardsSide[3] = new ModelRenderer(this, 0, 31);
        this.boardsSide[3].addBox(-14.5F, -2.0F, 0.0F, 28, 3, 1);
        this.boardsSide[3].setRotationPoint(11.0F, -5.0F, -1.0F);
        this.boardsSide[3].rotateAngleY = (float) Math.PI / 2.0F;

        this.boardsRear[0] = new ModelRenderer(this, 50, 35);
        this.boardsRear[0].addBox(10.0F, -12.0F, 13.5F, 2, 11, 1);

        this.boardsRear[1] = new ModelRenderer(this, 50, 35);
        this.boardsRear[1].addBox(-12.0F, -12.0F, 13.5F, 2, 11, 1);

        this.body.addChild(this.axis);
        this.body.addChild(this.shaft);
        this.body.addChild(this.boardBottom);
        this.body.addChild(this.boardFront);
        this.body.addChild(this.boardsRear[0]);
        this.body.addChild(this.boardsRear[1]);
        this.body.addChild(this.boardsSide[0]);
        this.body.addChild(this.boardsSide[1]);
        this.body.addChild(this.boardsSide[2]);
        this.body.addChild(this.boardsSide[3]);
    }
}
