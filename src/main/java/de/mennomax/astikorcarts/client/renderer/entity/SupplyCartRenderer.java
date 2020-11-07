package de.mennomax.astikorcarts.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.mennomax.astikorcarts.AstikorCarts;
import de.mennomax.astikorcarts.client.renderer.entity.model.SupplyCartModel;
import de.mennomax.astikorcarts.entity.SupplyCartEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.Objects;
import java.util.Random;

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
        boolean flower = false;
        for (final ItemStack itemStack : cargo) {
            if (itemStack.isEmpty()) continue;
            if (itemStack.getItem() instanceof BlockItem && itemStack.getItem().isIn(ItemTags.FLOWERS)) {
                flower = true;
            } else {
                flower = false;
                break;
            }
        }
        if (flower) {
            this.renderFlowers(entity, stack, source, packedLight, cargo);
        } else {
            this.renderSupplies(entity, stack, source, packedLight, cargo);
        }
        stack.pop();
    }

    private void renderFlowers(final SupplyCartEntity entity, final MatrixStack stack, final IRenderTypeBuffer source, final int packedLight, final NonNullList<ItemStack> cargo) {
        this.model.getFlowerBasket().render(stack, source.getBuffer(this.model.getRenderType(this.getEntityTexture(entity))), packedLight, OverlayTexture.NO_OVERLAY);
        final BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
        final BlockModelRenderer renderer = dispatcher.getBlockModelRenderer();
        for (int i = 0; i < cargo.size(); i++) {
            final ItemStack itemStack = cargo.get(i);
            if (!(itemStack.getItem() instanceof BlockItem)) continue;
            final int ix = i % 2, iz = i / 2;
            final BlockState defaultState = ((BlockItem) itemStack.getItem()).getBlock().getDefaultState();
            final BlockState state = defaultState.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF) ? defaultState.with(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER) : defaultState;
            IBakedModel model = dispatcher.getModelForState(state);
            final int rgb = Minecraft.getInstance().getBlockColors().getColor(state, null, null, 0);
            final float r = (float) (rgb >> 16 & 255) / 255.0F;
            final float g = (float) (rgb >> 8 & 255) / 255.0F;
            final float b = (float) (rgb & 255) / 255.0F;
            stack.push();
            stack.translate(0.0D, -0.7D, -3.0D / 16.0D);
            stack.scale(0.65F, 0.65F, 0.65F);
            stack.translate(ix, 0.5D, iz - 1.0D);
            stack.rotate(Vector3f.ZP.rotationDegrees(180.0F));
            renderer.renderModel(stack.getLast(), source.getBuffer(RenderType.getCutout()), state, model, r, g, b, packedLight, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
            stack.pop();
        }
    }

    private void renderSupplies(final SupplyCartEntity entity, final MatrixStack stack, final IRenderTypeBuffer source, final int packedLight, final NonNullList<ItemStack> cargo) {
        final ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
        final Random rng = new Random();
        for (int i = 0; i < cargo.size(); i++) {
            final ItemStack itemStack = cargo.get(i);
            if (itemStack.isEmpty()) continue;
            final int ix = i % 2, iz = i / 2;
            if (i < cargo.size() - 2 && cargo.get(i + 2).getItem().isIn(ItemTags.BEDS)) continue;
            if (i >= 2 && cargo.get(i - 2).getItem().isIn(ItemTags.BEDS)) continue;
            final double x = (ix - 0.5D) * 11.0D / 16.0D;
            final double z = (iz * 11.0D - 9.0D) / 16.0D;
            final IBakedModel model = renderer.getItemModelWithOverrides(itemStack, entity.world, null);
            stack.push();
            if (model.isGui3d()) {
                stack.translate(x, -0.46D, z);
                stack.scale(0.65F, 0.65F, 0.65F);
                stack.rotate(Vector3f.ZP.rotationDegrees(180.0F));
                if (iz < 1 && itemStack.getItem().isIn(ItemTags.BEDS)) {
                    stack.translate(0.0D, 0.0D, 1.0D);
                } else if (!model.isBuiltInRenderer()) {
                    stack.rotate(Vector3f.YP.rotationDegrees(180.0F));
                }
                renderer.renderItem(itemStack, ItemCameraTransforms.TransformType.NONE, false, stack, source, packedLight, OverlayTexture.NO_OVERLAY, model);
            } else {
                rng.setSeed(32L * i + Objects.hashCode(itemStack.getItem().getRegistryName()));
                stack.translate(x, -0.15D + ((ix + iz) % 2 == 0 ? 0.0D : 1.0e-4D), z);
                stack.scale(0.7F, 0.7F, 0.7F);
                stack.rotate(Vector3f.YP.rotation(rng.nextFloat() * (float) Math.PI));
                stack.rotate(Vector3f.XP.rotationDegrees(-90.0F));
                final int copies = Math.min(itemStack.getCount(), (itemStack.getCount() - 1) / 16 + 2);
                renderer.renderItem(itemStack, ItemCameraTransforms.TransformType.NONE, false, stack, source, packedLight, OverlayTexture.NO_OVERLAY, model);
                for (int n = 1; n < copies; n++) {
                    stack.push();
                    stack.rotate(Vector3f.ZP.rotation(rng.nextFloat() * (float) Math.PI));
                    stack.translate((rng.nextFloat() * 2.0F - 1.0F) * 0.05F, (rng.nextFloat() * 2.0F - 1.0F) * 0.05F, -0.1D * n);
                    renderer.renderItem(itemStack, ItemCameraTransforms.TransformType.NONE, false, stack, source, packedLight, OverlayTexture.NO_OVERLAY, model);
                    stack.pop();
                }
            }
            stack.pop();
        }
    }

    @Override
    public ResourceLocation getEntityTexture(final SupplyCartEntity entity) {
        return TEXTURE;
    }
}
