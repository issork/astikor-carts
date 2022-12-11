package de.mennomax.astikorcarts.client.renderer.entity.model;

import de.mennomax.astikorcarts.entity.SupplyCartEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;

public final class SupplyCartModel extends CartModel<SupplyCartEntity> {
    private final ModelPart flowerBasket;

    public SupplyCartModel(final ModelPart root) {
        super(root);

        this.flowerBasket = root.getChild("flowerBasket");
    }

    public ModelPart getFlowerBasket() {
        return this.flowerBasket;
    }

    public static LayerDefinition createLayer() {
        final MeshDefinition def = CartModel.createDefinition();

        final EasyMeshBuilder boardBottom = new EasyMeshBuilder("boardBottom", 0, 0);
        boardBottom.addBox(-15.5F, -11.0F, -2.0F, 29, 22, 1);
        boardBottom.yRot = (float) Math.PI / -2.0F;
        boardBottom.xRot = (float) Math.PI / -2.0F;

        final EasyMeshBuilder axis = new EasyMeshBuilder("axis", 0, 23);
        axis.addBox(-12.5F, -1.0F, -1.0F, 25, 2, 2);

        final EasyMeshBuilder shaft = new EasyMeshBuilder("shaft", 0, 31);
        shaft.setRotationPoint(0.0F, -5.0F, -15.0F);
        shaft.yRot = (float) Math.PI / 2.0F;
        shaft.addBox(0.0F, -2.5F, -8.0F, 20, 2, 1);
        shaft.addBox(0.0F, -2.5F, 7.0F, 20, 2, 1);

        final EasyMeshBuilder boardFront = new EasyMeshBuilder("boardFront", 0, 34);
        boardFront.addBox(-12.0F, -12.0F, -15.5F, 24, 10, 1);

        final EasyMeshBuilder[] boardsSide = new EasyMeshBuilder[4];
        boardsSide[0] = new EasyMeshBuilder("boards_side_0", 0, 27);
        boardsSide[0].addBox(-13.5F, -7.0F, 0.0F, 28, 3, 1);
        boardsSide[0].setRotationPoint(-11.0F, -5.0F, -1.0F);
        boardsSide[0].yRot = (float) Math.PI / -2.0F;

        boardsSide[1] = new EasyMeshBuilder("boards_side_1", 0, 27);
        boardsSide[1].addBox(-14.5F, -7.0F, 0.0F, 28, 3, 1);
        boardsSide[1].setRotationPoint(11.0F, -5.0F, -1.0F);
        boardsSide[1].yRot = (float) Math.PI / 2.0F;

        boardsSide[2] = new EasyMeshBuilder("boards_side_2", 0, 27);
        boardsSide[2].addBox(-13.5F, -2.0F, 0.0F, 28, 3, 1);
        boardsSide[2].setRotationPoint(-11.0F, -5.0F, -1.0F);
        boardsSide[2].yRot = (float) Math.PI / -2.0F;

        boardsSide[3] = new EasyMeshBuilder("boards_side_3", 0, 27);
        boardsSide[3].addBox(-14.5F, -2.0F, 0.0F, 28, 3, 1);
        boardsSide[3].setRotationPoint(11.0F, -5.0F, -1.0F);
        boardsSide[3].yRot = (float) Math.PI / 2.0F;

        final EasyMeshBuilder[] boardsRear = new EasyMeshBuilder[2];
        boardsRear[0] = new EasyMeshBuilder("boards_rear_0", 50, 31);
        boardsRear[0].addBox(10.0F, -12.0F, 13.5F, 2, 11, 1);

        boardsRear[1] = new EasyMeshBuilder("boards_rear_1", 50, 31);
        boardsRear[1].addBox(-12.0F, -12.0F, 13.5F, 2, 11, 1);

        final EasyMeshBuilder body = CartModel.createBody();
        body.addChild(axis);
        body.addChild(shaft);
        body.addChild(boardBottom);
        body.addChild(boardFront);
        body.addChild(boardsRear[0]);
        body.addChild(boardsRear[1]);
        body.addChild(boardsSide[0]);
        body.addChild(boardsSide[1]);
        body.addChild(boardsSide[2]);
        body.addChild(boardsSide[3]);
        body.build(def.getRoot());

        final EasyMeshBuilder flowerBasket = new EasyMeshBuilder("flowerBasket");
        flowerBasket.setTextureOffset(-17, 45).addBox(-8.0F, -6.0F, -11.5F, 16.0F, 1.0F, 17.0F);
        flowerBasket.setTextureOffset(16, 45).addBox(-10.0F, -7.0F, 5.5F, 20.0F, 5.0F, 2.0F);
        final EasyMeshBuilder frontSide = new EasyMeshBuilder("frontSide", 16, 45);
        frontSide.yRot = (float) Math.PI;
        frontSide.addBox(-10.0F, -7.0F, 11.5F, 20.0F, 5.0F, 2.0F);
        flowerBasket.addChild(frontSide);
        final EasyMeshBuilder leftSide = new EasyMeshBuilder("leftSide", 16, 52);
        leftSide.yRot = (float) Math.PI / 2.0F;
        leftSide.addBox(-5.5F, -7.0F, 8.0F, 17.0F, 5.0F, 2.0F);
        flowerBasket.addChild(leftSide);
        final EasyMeshBuilder rightSide = new EasyMeshBuilder("rightSide", 16, 52);
        rightSide.yRot = (float) -Math.PI / 2.0F;
        rightSide.addBox(-11.5F, -7.0F, 8.0F, 17.0F, 5.0F, 2.0F);
        flowerBasket.addChild(rightSide);
        flowerBasket.build(def.getRoot());

        return LayerDefinition.create(def, 64, 64);
    }
}
