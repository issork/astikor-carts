package de.mennomax.astikorcarts.client.renderer.entity.model;

import de.mennomax.astikorcarts.entity.CargoCartEntity;
import net.minecraft.client.renderer.entity.model.RendererModel;

public final class CargoCartModel extends CartModel<CargoCartEntity> {
    private final RendererModel boardBottom;
    private final RendererModel axis;
    private final RendererModel shaft;
    private final RendererModel boardFront;
    private final RendererModel[] boardsSide = new RendererModel[4];
    private final RendererModel[] boardsRear = new RendererModel[2];
    private final RendererModel[] cargo = new RendererModel[4];

    public CargoCartModel() {
        super(128, 64);

        this.boardBottom = new RendererModel(this, 0, 0);
        this.boardBottom.addBox(-15.5F, -11.0F, -2.0F, 29, 22, 1);
        this.boardBottom.rotateAngleY = (float) Math.PI / -2.0F;
        this.boardBottom.rotateAngleX = (float) Math.PI / -2.0F;

        this.axis = new RendererModel(this, 0, 23);
        this.axis.addBox(-12.5F, -1.0F, -1.0F, 25, 2, 2);

        this.shaft = new RendererModel(this, 0, 35);
        this.shaft.setRotationPoint(0.0F, -5.0F, -15.0F);
        this.shaft.rotateAngleY = (float) Math.PI / 2.0F;
        this.shaft.addBox(0.0F, -2.5F, -8.0F, 20, 2, 1);
        this.shaft.addBox(0.0F, -2.5F, 7.0F, 20, 2, 1);

        this.boardFront = new RendererModel(this, 0, 38);
        this.boardFront.addBox(-12.0F, -12.0F, -15.5F, 24, 10, 1);

        this.boardsSide[0] = new RendererModel(this, 0, 27);
        this.boardsSide[0].addBox(-13.5F, -7.0F, 0.0F, 28, 3, 1);
        this.boardsSide[0].setRotationPoint(-11.0F, -5.0F, -1.0F);
        this.boardsSide[0].rotateAngleY = (float) Math.PI / -2.0F;

        this.boardsSide[1] = new RendererModel(this, 0, 27);
        this.boardsSide[1].addBox(-14.5F, -7.0F, 0.0F, 28, 3, 1);
        this.boardsSide[1].setRotationPoint(11.0F, -5.0F, -1.0F);
        this.boardsSide[1].rotateAngleY = (float) Math.PI / 2.0F;

        this.boardsSide[2] = new RendererModel(this, 0, 31);
        this.boardsSide[2].addBox(-13.5F, -2.0F, 0.0F, 28, 3, 1);
        this.boardsSide[2].setRotationPoint(-11.0F, -5.0F, -1.0F);
        this.boardsSide[2].rotateAngleY = (float) Math.PI / -2.0F;

        this.boardsSide[3] = new RendererModel(this, 0, 31);
        this.boardsSide[3].addBox(-14.5F, -2.0F, 0.0F, 28, 3, 1);
        this.boardsSide[3].setRotationPoint(11.0F, -5.0F, -1.0F);
        this.boardsSide[3].rotateAngleY = (float) Math.PI / 2.0F;

        this.boardsRear[0] = new RendererModel(this, 50, 35);
        this.boardsRear[0].addBox(10.0F, -12.0F, 13.5F, 2, 11, 1);

        this.boardsRear[1] = new RendererModel(this, 50, 35);
        this.boardsRear[1].addBox(-12.0F, -12.0F, 13.5F, 2, 11, 1);

        this.cargo[0] = new RendererModel(this, 66, 38);
        this.cargo[0].addBox(-9.5F, -5.0F, -12.5F, 8, 8, 8);
        this.cargo[0].setRotationPoint(0.0F, -5.0F, -1.0F);
        this.cargo[0].rotateAngleY = 0.05F;

        this.cargo[1] = new RendererModel(this, 66, 18);
        this.cargo[1].addBox(-1.0F, -7.0F, -12.5F, 11, 10, 10);
        this.cargo[1].setRotationPoint(0.0F, -5.0F, -1.0F);

        this.cargo[2] = new RendererModel(this, 66, 0);
        this.cargo[2].addBox(-10.5F, -5.0F, -8.5F, 20, 8, 10);
        this.cargo[2].setRotationPoint(0.0F, -5.0F, -1.0F);
        this.cargo[2].rotateAngleY = (float) Math.PI;

        this.cargo[3] = new RendererModel(this, 66, 54);
        this.cargo[3].addBox(-12.0F, -7.0F, 1.0F, 20, 2, 3);
        this.cargo[3].setRotationPoint(0.0F, -5.0F, -1.0F);
        this.cargo[3].rotateAngleY = -1.067F;

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
        this.body.addChild(this.cargo[0]);
        this.body.addChild(this.cargo[1]);
        this.body.addChild(this.cargo[2]);
        this.body.addChild(this.cargo[3]);
    }

    @Override
    public void render(final CargoCartEntity entity, final float delta, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float pitch, final float scale) {
        for (int i = 0; i < this.cargo.length; i++) {
            this.cargo[i].showModel = i < entity.getCargo();
        }
        super.render(entity, delta, limbSwingAmount, ageInTicks, netHeadYaw, pitch, scale);
    }
}
