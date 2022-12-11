package de.mennomax.astikorcarts.client.renderer.entity.model;

import de.mennomax.astikorcarts.entity.AnimalCartEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;

public final class AnimalCartModel extends CartModel<AnimalCartEntity> {
    public AnimalCartModel(final ModelPart root) {
        super(root);
    }

    public static LayerDefinition createLayer() {
        final MeshDefinition def = CartModel.createDefinition();

        final EasyMeshBuilder axis = new EasyMeshBuilder("axis", 0, 21);
        axis.addBox(-12.5F, -1.0F, -1.0F, 25, 2, 2);

        final EasyMeshBuilder cartBase = new EasyMeshBuilder("cartBase", 0, 0);
        cartBase.addBox(-15.5F, -10.0F, -2.0F, 29, 20, 1);
        cartBase.xRot = (float) -Math.PI / 2.0F;
        cartBase.yRot = (float) -Math.PI / 2.0F;

        final EasyMeshBuilder shaft = new EasyMeshBuilder("shaft", 0, 25);
        shaft.setRotationPoint(0.0F, -5.0F, -15.0F);
        shaft.yRot = (float) Math.PI / 2.0F;
        shaft.addBox(0.0F, -0.5F, -8.0F, 20, 2, 1);
        shaft.addBox(0.0F, -0.5F, 7.0F, 20, 2, 1);

        final EasyMeshBuilder boardLeft = new EasyMeshBuilder("boardLeft", 0, 28);
        boardLeft.addBox(-10.0F, -14.5F, 9F, 8, 31, 2);
        boardLeft.xRot = (float) -Math.PI / 2.0F;
        boardLeft.zRot = (float) Math.PI / 2.0F;

        final EasyMeshBuilder boardRight = new EasyMeshBuilder("boardRight", 0, 28);
        boardRight.addBox(-10.0F, -14.5F, -11F, 8, 31, 2);
        boardRight.xRot = (float) -Math.PI / 2.0F;
        boardRight.zRot = (float) Math.PI / 2.0F;

        final EasyMeshBuilder boardBack = new EasyMeshBuilder("boardBack", 20, 28);
        boardBack.addBox(-9F, -10.0F, 12.5F, 18, 8, 2);

        final EasyMeshBuilder boardFront = new EasyMeshBuilder("boardFront", 20, 28);
        boardFront.addBox(-9F, -10.0F, -16.5F, 18, 8, 2);

        final EasyMeshBuilder body = CartModel.createBody();
        body.addChild(axis);
        body.addChild(cartBase);
        body.addChild(shaft);
        body.addChild(boardLeft);
        body.addChild(boardRight);
        body.addChild(boardBack);
        body.addChild(boardFront);
        body.build(def.getRoot());

        return LayerDefinition.create(def, 64, 64);
    }
}
