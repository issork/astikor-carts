package de.mennomax.astikorcarts.client.renderer.entity.model;

import de.mennomax.astikorcarts.entity.MobCartEntity;
import net.minecraft.client.renderer.model.ModelRenderer;

public final class MobCartModel extends CartModel<MobCartEntity> {
    private final ModelRenderer axis;
    private final ModelRenderer cartBase;
    private final ModelRenderer shaft;
    private final ModelRenderer boardLeft;
    private final ModelRenderer boardRight;
    private final ModelRenderer boardBack;
    private final ModelRenderer boardFront;

    public MobCartModel() {
        super(64, 64);

        this.axis = new ModelRenderer(this, 0, 21);
        this.axis.addBox(-12.5F, -1.0F, -1.0F, 25, 2, 2);

        this.cartBase = new ModelRenderer(this, 0, 0);
        this.cartBase.addBox(-15.5F, -10.0F, -2.0F, 29, 20, 1);
        this.cartBase.rotateAngleX = (float) -Math.PI / 2.0F;
        this.cartBase.rotateAngleY = (float) -Math.PI / 2.0F;

        this.shaft = new ModelRenderer(this, 0, 25);
        this.shaft.setRotationPoint(0.0F, -5.0F, -15.0F);
        this.shaft.rotateAngleY = (float) Math.PI / 2.0F;
        this.shaft.addBox(0.0F, -0.5F, -8.0F, 20, 2, 1);
        this.shaft.addBox(0.0F, -0.5F, 7.0F, 20, 2, 1);

        this.boardLeft = new ModelRenderer(this, 0, 28);
        this.boardLeft.addBox(-10.0F, -14.5F, 9F, 8, 31, 2);
        this.boardLeft.rotateAngleX = (float) -Math.PI / 2.0F;
        this.boardLeft.rotateAngleZ = (float) Math.PI / 2.0F;

        this.boardRight = new ModelRenderer(this, 0, 28);
        this.boardRight.addBox(-10.0F, -14.5F, -11F, 8, 31, 2);
        this.boardRight.rotateAngleX = (float) -Math.PI / 2.0F;
        this.boardRight.rotateAngleZ = (float) Math.PI / 2.0F;

        this.boardBack = new ModelRenderer(this, 20, 28);
        this.boardBack.addBox(-9F, -10.0F, 12.5F, 18, 8, 2);

        this.boardFront = new ModelRenderer(this, 20, 28);
        this.boardFront.addBox(-9F, -10.0F, -16.5F, 18, 8, 2);

        this.body.addChild(this.axis);
        this.body.addChild(this.cartBase);
        this.body.addChild(this.shaft);
        this.body.addChild(this.boardLeft);
        this.body.addChild(this.boardRight);
        this.body.addChild(this.boardBack);
        this.body.addChild(this.boardFront);
        this.body.setRotationPoint(0.0F, -11.0F, 1.0F);
    }
}
