package de.mennomax.astikorcarts.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.mennomax.astikorcarts.AstikorCarts;
import de.mennomax.astikorcarts.client.renderer.entity.model.SupplyCartModel;
import de.mennomax.astikorcarts.entity.SupplyCartEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

import java.util.Objects;

public final class SupplyCartRenderer extends DrawnRenderer<SupplyCartEntity, SupplyCartModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AstikorCarts.ID, "textures/entity/supply_cart.png");

    public SupplyCartRenderer(final EntityRendererManager renderManager) {
        super(renderManager, new SupplyCartModel());
        this.shadowSize = 1.0F;
    }

    @Override
    protected void renderContents(final SupplyCartEntity entity, final float delta, final MatrixStack stack, final IRenderTypeBuffer source, final int packedLight) {
        super.renderContents(entity, delta, stack, source, packedLight);
        final NonNullList<ItemStack> cargo = entity.getCargo();
        stack.push();
        this.model.getBody().translateRotate(stack);
        for (int i = 0; i < cargo.size(); i++) {
            final ItemStack itemStack = cargo.get(i);
            if (itemStack.isEmpty()) continue;
            final int ix = i % 2, iz = i / 2;
            if (i < cargo.size() - 2 && cargo.get(i + 2).getItem().isIn(ItemTags.BEDS)) continue;
            if (i >= 2 && cargo.get(i - 2).getItem().isIn(ItemTags.BEDS)) continue;
            final double x = (ix - 0.5D) * 11.0D / 16.0D;
            final double z = (iz * 11.0D - 9.0D) / 16.0D;
            final ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
            final IBakedModel model = renderer.getItemModelWithOverrides(itemStack, null, null);
            stack.push();
            if (model.isGui3d()) {
                stack.translate(x, -0.46D, z);
                stack.scale(0.65F, 0.65F, 0.65F);
                stack.rotate(Vector3f.ZP.rotationDegrees(180.0F));
                if (iz < 1 && itemStack.getItem().isIn(ItemTags.BEDS)) stack.translate(0.0D, 0.0D, 1.0D);
            } else {
                stack.translate(x, -0.15D + ((ix + iz) % 2 == 0 ? 0.0D : 1.0e-4D), z);
                stack.scale(0.7F, 0.7F, 0.7F);
                stack.rotate(Vector3f.YP.rotationDegrees(Objects.hashCode(itemStack.getItem().getRegistryName()) / 65536.0F));
                stack.rotate(Vector3f.XP.rotationDegrees(-90.0F));
            }
            renderer.renderItem(itemStack, ItemCameraTransforms.TransformType.NONE, false, stack, source, packedLight, OverlayTexture.NO_OVERLAY, model);
            stack.pop();
        }
        stack.pop();
    }

    @Override
    public ResourceLocation getEntityTexture(final SupplyCartEntity entity) {
        return TEXTURE;
    }
}
