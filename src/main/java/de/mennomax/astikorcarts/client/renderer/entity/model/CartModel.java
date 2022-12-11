package de.mennomax.astikorcarts.client.renderer.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.mennomax.astikorcarts.entity.AbstractDrawnEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.util.Mth;

public abstract class CartModel<T extends AbstractDrawnEntity> extends EntityModel<T> {
    protected final ModelPart body;

    protected final ModelPart leftWheel;

    protected final ModelPart rightWheel;

    protected CartModel(final ModelPart root) {
        this.body = root.getChild("body");
        this.leftWheel = root.getChild("leftWheel");
        this.rightWheel = root.getChild("rightWheel");
    }

    public ModelPart getBody() {
        return this.body;
    }

    public ModelPart getWheel() {
        return this.rightWheel;
    }

    @Override
    public void renderToBuffer(final PoseStack stack, final VertexConsumer buf, final int packedLight, final int packedOverlay, final float red, final float green, final float blue, final float alpha) {
        this.body.render(stack, buf, packedLight, packedOverlay, red, green, blue, alpha);
        this.leftWheel.render(stack, buf, packedLight, packedOverlay, red, green, blue, alpha);
        this.rightWheel.render(stack, buf, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void setupAnim(final T entity, final float delta, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float pitch) {
        this.body.xRot = (float) Math.toRadians(pitch);
        this.rightWheel.xRot = (float) (entity.getWheelRotation(0) + entity.getWheelRotationIncrement(0) * delta);
        this.leftWheel.xRot = (float) (entity.getWheelRotation(1) + entity.getWheelRotationIncrement(1) * delta);
        final float time = entity.getTimeSinceHit() - delta;
        final float rot;
        if (time > 0.0F) {
            final float damage = Math.max(entity.getDamageTaken() - delta, 0.0F);
            rot = (float) Math.toRadians(Mth.sin(time) * time * damage / 40.0F * -entity.getForwardDirection());
        } else {
            rot = 0.0F;
        }
        this.rightWheel.zRot = rot;
        this.leftWheel.zRot = rot;
    }

    public static MeshDefinition createDefinition() {
        final MeshDefinition def = new MeshDefinition();

        final EasyMeshBuilder leftWheel = new EasyMeshBuilder("leftWheel", 46, 60);
        leftWheel.setRotationPoint(14.5F, -11.0F, 1.0F);
        leftWheel.addBox(-2.0F, -1.0F, -1.0F, 2, 2, 2);
        for (int i = 0; i < 8; i++) {
            final EasyMeshBuilder rim = new EasyMeshBuilder("rim_" + i, 58, 54);
            rim.addBox(-2.0F, -4.5F, 9.86F, 2, 9, 1);
            rim.xRot = i * (float) Math.PI / 4.0F;
            leftWheel.addChild(rim);

            final EasyMeshBuilder spoke = new EasyMeshBuilder("spoke_" + i, 54, 54);
            spoke.addBox(-1.5F, 1.0F, -0.5F, 1, 9, 1);
            spoke.xRot = i * (float) Math.PI / 4.0F;
            leftWheel.addChild(spoke);
        }
        leftWheel.build(def.getRoot());

        final EasyMeshBuilder rightWheel = new EasyMeshBuilder("rightWheel", 46, 60);
        rightWheel.setRotationPoint(-14.5F, -11.0F, 1.0F);
        rightWheel.addBox(0.0F, -1.0F, -1.0F, 2, 2, 2);
        for (int i = 0; i < 8; i++) {
            final EasyMeshBuilder rim = new EasyMeshBuilder("rim_" + i, 58, 54);
            rim.addBox(0.0F, -4.5F, 9.86F, 2, 9, 1);
            rim.xRot = i * (float) Math.PI / 4.0F;
            rightWheel.addChild(rim);

            final EasyMeshBuilder spoke = new EasyMeshBuilder("spoke_" + i, 54, 54);
            spoke.addBox(0.5F, 1.0F, -0.5F, 1, 9, 1);
            spoke.xRot = i * (float) Math.PI / 4.0F;
            rightWheel.addChild(spoke);
        }
        rightWheel.build(def.getRoot());

        return def;
    }

    public static EasyMeshBuilder createBody() {
        final EasyMeshBuilder body = new EasyMeshBuilder("body");
        body.setRotationPoint(0.0F, -11.0F, 1.0F);
        return body;
    }
}
