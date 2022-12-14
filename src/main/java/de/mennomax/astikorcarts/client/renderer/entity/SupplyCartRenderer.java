package de.mennomax.astikorcarts.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import de.mennomax.astikorcarts.AstikorCarts;
import de.mennomax.astikorcarts.client.renderer.AstikorCartsModelLayers;
import de.mennomax.astikorcarts.client.renderer.entity.model.SupplyCartModel;
import de.mennomax.astikorcarts.entity.SupplyCartEntity;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.PaintingTextureManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeableArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class SupplyCartRenderer extends DrawnRenderer<SupplyCartEntity, SupplyCartModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AstikorCarts.ID, "textures/entity/supply_cart.png");

    // access to use the forge code for armor texture which is an instance method for some reason
    private static final HumanoidArmorLayer<LivingEntity, HumanoidModel<LivingEntity>, HumanoidModel<LivingEntity>> DUMMY = new HumanoidArmorLayer<>(null, null, null);

    private final HumanoidModel<LivingEntity> leggings, armor;

    public SupplyCartRenderer(final EntityRendererProvider.Context renderManager) {
        super(renderManager, new SupplyCartModel(renderManager.bakeLayer(AstikorCartsModelLayers.SUPPLY_CART)));
        this.leggings = new HumanoidModel<>(renderManager.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR));
        this.armor = new HumanoidModel<>(renderManager.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR));
        this.shadowRadius = 1.0F;
    }

    @Override
    protected void renderContents(final SupplyCartEntity entity, final float delta, final PoseStack stack, final MultiBufferSource source, final int packedLight) {
        super.renderContents(entity, delta, stack, source, packedLight);
        final NonNullList<ItemStack> cargo = entity.getCargo();
        Contents contents = Contents.SUPPLIES;
        final Iterator<ItemStack> it = cargo.iterator();
        outer:
        while (it.hasNext()) {
            final ItemStack s = it.next();
            if (s.isEmpty()) continue;
            for (final Contents c : Contents.values()) {
                if (c.predicate.test(s)) {
                    contents = c;
                    break outer;
                }
            }
        }
        while (contents != Contents.SUPPLIES && it.hasNext()) {
            final ItemStack s = it.next();
            if (s.isEmpty()) continue;
            if (!contents.predicate.test(s)) contents = Contents.SUPPLIES;
        }
        stack.pushPose();
        this.model.getBody().translateAndRotate(stack);
        contents.renderer.render(this, entity, stack, source, packedLight, cargo);
        final List<Pair<Holder<BannerPattern>, DyeColor>> list = entity.getBannerPattern();
        if (!list.isEmpty()) {
            stack.translate(0.0D, -0.6D, 1.5D);
            this.renderBanner(stack, source, packedLight, list);
        }
        stack.popPose();
    }

    private void renderFlowers(final SupplyCartEntity entity, final PoseStack stack, final MultiBufferSource source, final int packedLight, final NonNullList<ItemStack> cargo) {
        this.model.getFlowerBasket().render(stack, source.getBuffer(this.model.renderType(this.getTextureLocation(entity))), packedLight, OverlayTexture.NO_OVERLAY);
        final BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        final ModelBlockRenderer renderer = dispatcher.getModelRenderer();
        for (int i = 0; i < cargo.size(); i++) {
            final ItemStack itemStack = cargo.get(i);
            if (!(itemStack.getItem() instanceof BlockItem)) continue;
            final int ix = i % 2, iz = i / 2;
            final BlockState defaultState = ((BlockItem) itemStack.getItem()).getBlock().defaultBlockState();
            final BlockState state = defaultState.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF) ? defaultState.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER) : defaultState;
            final BakedModel model = dispatcher.getBlockModel(state);
            final int rgb = Minecraft.getInstance().getBlockColors().getColor(state, null, null, 0);
            final float r = (float) (rgb >> 16 & 255) / 255.0F;
            final float g = (float) (rgb >> 8 & 255) / 255.0F;
            final float b = (float) (rgb & 255) / 255.0F;
            stack.pushPose();
            stack.translate(0.0D, -0.7D, -3.0D / 16.0D);
            stack.scale(0.65F, 0.65F, 0.65F);
            stack.translate(ix, 0.5D, iz - 1.0D);
            stack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
            renderer.renderModel(stack.last(), source.getBuffer(RenderType.cutout()), state, model, r, g, b, packedLight, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);
            stack.popPose();
        }
    }

    private void renderWheel(final SupplyCartEntity entity, final PoseStack stack, final MultiBufferSource source, final int packedLight, final NonNullList<ItemStack> cargo) {
        stack.translate(1.18D, 0.1D, -0.15D);
        final ModelPart wheel = this.model.getWheel();
        wheel.xRot = 0.9F;
        wheel.zRot = (float) Math.PI * 0.3F;
        wheel.render(stack, source.getBuffer(this.model.renderType(this.getTextureLocation(entity))), packedLight, OverlayTexture.NO_OVERLAY);
    }

    private void renderPaintings(final SupplyCartEntity entity, final PoseStack stack, final MultiBufferSource source, final int packedLight, final NonNullList<ItemStack> cargo) {
        final VertexConsumer buf = source.getBuffer(RenderType.entitySolid(Minecraft.getInstance().getPaintingTextures().getBackSprite().atlas().location()));
        final ObjectList<PaintingVariant> types = StreamSupport.stream(ForgeRegistries.PAINTING_VARIANTS.spliterator(), false)
            .filter(t -> t.getWidth() == 16 && t.getHeight() == 16)
            .collect(Collectors.toCollection(ObjectArrayList::new));
        final Random rng = new Random(entity.getUUID().getMostSignificantBits() ^ entity.getUUID().getLeastSignificantBits());
        ObjectLists.shuffle(types, rng);
        int count = 0;
        for (final ItemStack itemStack : cargo) {
            if (itemStack.isEmpty()) continue;
            count++;
        }
        stack.translate(0.0D, -2.5D / 16.0D, 0.0D);
        stack.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
        for (int i = 0, n = 0; i < cargo.size(); i++) {
            final ItemStack itemStack = cargo.get(i);
            if (itemStack.isEmpty()) continue;
            final PaintingVariant t = types.get(i % types.size());
            stack.pushPose();
            stack.translate(0.0D, (n++ - (count - 1) * 0.5D) / count, -1.0D / 16.0D * i);
            stack.mulPose(Vector3f.ZP.rotation(rng.nextFloat() * (float) Math.PI));
            this.renderPainting(t, stack, buf, packedLight);
            stack.popPose();
        }
    }

    private void renderSupplies(final SupplyCartEntity entity, final PoseStack stack, final MultiBufferSource source, final int packedLight, final NonNullList<ItemStack> cargo) {
        final ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
        final Random rng = new Random();
        for (int i = 0; i < cargo.size(); i++) {
            final ItemStack itemStack = cargo.get(i);
            if (itemStack.isEmpty()) continue;
            final int ix = i % 2, iz = i / 2;
            if (i < cargo.size() - 2 && cargo.get(i + 2).is(ItemTags.BEDS)) continue;
            if (i >= 2 && cargo.get(i - 2).is(ItemTags.BEDS)) continue;
            final double x = (ix - 0.5D) * 11.0D / 16.0D;
            final double z = (iz * 11.0D - 9.0D) / 16.0D;
            final BakedModel model = renderer.getModel(itemStack, entity.level, null, i);
            stack.pushPose();
            if (model.isGui3d() && itemStack.getItem() != Items.TRIDENT) {
                stack.translate(x, -0.46D, z);
                stack.scale(0.65F, 0.65F, 0.65F);
                stack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
                if (itemStack.getItem() == Items.SHIELD) {
                    stack.scale(1.2F, 1.2F, 1.2F);
                    stack.mulPose(Vector3f.YP.rotationDegrees(ix == 0 ? -90.0F : 90.0F));
                    stack.translate(0.5D, 0.8D, -0.05D);
                    stack.mulPose(Vector3f.XP.rotationDegrees(-22.5F));
                } else if (iz < 1 && itemStack.is(ItemTags.BEDS)) {
                    stack.translate(0.0D, 0.0D, 1.0D);
                } else if (!model.isCustomRenderer()) {
                    stack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
                }
                renderer.render(itemStack, ItemTransforms.TransformType.NONE, false, stack, source, packedLight, OverlayTexture.NO_OVERLAY, model);
            } else {
                rng.setSeed(32L * i + Objects.hashCode(ForgeRegistries.ITEMS.getKey(itemStack.getItem())));
                stack.translate(x, -0.15D + ((ix + iz) % 2 == 0 ? 0.0D : 1.0e-4D), z);
                if (ArmorItem.class.equals(itemStack.getItem().getClass()) || DyeableArmorItem.class.equals(itemStack.getItem().getClass())) {
                    this.renderArmor(entity, stack, source, packedLight, itemStack, ix);
                } else {
                    stack.scale(0.7F, 0.7F, 0.7F);
                    stack.mulPose(Vector3f.YP.rotation(rng.nextFloat() * (float) Math.PI));
                    stack.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
                    final int copies = Math.min(itemStack.getCount(), (itemStack.getCount() - 1) / 16 + 2);
                    renderer.render(itemStack, ItemTransforms.TransformType.FIXED, false, stack, source, packedLight, OverlayTexture.NO_OVERLAY, model);
                    for (int n = 1; n < copies; n++) {
                        stack.pushPose();
                        stack.mulPose(Vector3f.ZP.rotation(rng.nextFloat() * (float) Math.PI));
                        stack.translate((rng.nextFloat() * 2.0F - 1.0F) * 0.05F, (rng.nextFloat() * 2.0F - 1.0F) * 0.05F, -0.1D * n);
                        renderer.render(itemStack, ItemTransforms.TransformType.FIXED, false, stack, source, packedLight, OverlayTexture.NO_OVERLAY, model);
                        stack.popPose();
                    }
                }
            }
            stack.popPose();
        }
    }

    private void renderArmor(final SupplyCartEntity entity, final PoseStack stack, final MultiBufferSource source, final int packedLight, final ItemStack itemStack, final int ix) {
        final Item item = itemStack.getItem();
        if (!(item instanceof final ArmorItem armor)) return;
        final EquipmentSlot slot = LivingEntity.getEquipmentSlotForItem(itemStack);
        final HumanoidModel<LivingEntity> m = slot == EquipmentSlot.LEGS ? this.leggings : this.armor;
        stack.mulPose(Vector3f.YP.rotation(ix == 0 ? (float) Math.PI * 0.5F : (float) -Math.PI * 0.5F));
        m.setAllVisible(false);
        m.leftArmPose = HumanoidModel.ArmPose.EMPTY;
        m.rightArmPose = HumanoidModel.ArmPose.EMPTY;
        m.crouching = false;
        m.swimAmount = 0.0F;
        m.young = false;
        switch (slot) {
            case HEAD:
                stack.translate(0.0D, 0.1D, 0.0D);
                m.head.xRot = 0.2F;
                m.hat.copyFrom(m.head);
                m.head.visible = true;
                m.hat.visible = true;
                break;
            case CHEST:
                stack.translate(0.0D, -0.4D, -0.15D);
                m.leftArm.xRot = -0.15F;
                m.rightArm.xRot = -0.15F;
                m.body.xRot = 0.9F;
                m.body.visible = true;
                m.rightArm.visible = true;
                m.leftArm.visible = true;
                break;
            case LEGS:
                stack.translate(0.0D, -0.7D, -0.15D);
                m.body.xRot = 0.0F;
                m.rightLeg.xRot = 1.2F;
                m.leftLeg.xRot = 1.2F;
                m.rightLeg.yRot = -0.3F;
                m.leftLeg.yRot = 0.3F;
                m.body.visible = true;
                m.rightLeg.visible = true;
                m.leftLeg.visible = true;
                break;
            case FEET:
                stack.translate(0.0D, -1.15D, -0.1D);
                m.rightLeg.xRot = 0.0F;
                m.leftLeg.xRot = 0.0F;
                m.rightLeg.yRot = -0.1F;
                m.leftLeg.yRot = 0.0F;
                m.rightLeg.visible = true;
                m.leftLeg.visible = true;
                break;
        }
        stack.scale(0.75F, 0.75F, 0.75F);
        final VertexConsumer armorBuf = ItemRenderer.getArmorFoilBuffer(source,
            RenderType.armorCutoutNoCull(DUMMY.getArmorResource(entity, itemStack, slot, null)),
            false,
            itemStack.hasFoil()
        );
        if (armor instanceof DyeableArmorItem) {
            final int rgb = ((DyeableArmorItem) armor).getColor(itemStack);
            final float r = (float) (rgb >> 16 & 255) / 255.0F;
            final float g = (float) (rgb >> 8 & 255) / 255.0F;
            final float b = (float) (rgb & 255) / 255.0F;
            m.renderToBuffer(stack, armorBuf, packedLight, OverlayTexture.NO_OVERLAY, r, g, b, 1.0F);
            final VertexConsumer overlayBuf = ItemRenderer.getArmorFoilBuffer(source,
                RenderType.armorCutoutNoCull(DUMMY.getArmorResource(entity, itemStack, slot, "overlay")),
                false,
                itemStack.hasFoil()
            );
            m.renderToBuffer(stack, overlayBuf, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        } else {
            m.renderToBuffer(stack, armorBuf, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    private void renderPainting(final PaintingVariant painting, final PoseStack stack, final VertexConsumer buf, final int packedLight) {
        final PaintingTextureManager uploader = Minecraft.getInstance().getPaintingTextures();
        final int width = painting.getWidth();
        final int height = painting.getHeight();
        final TextureAtlasSprite art = uploader.get(painting);
        final TextureAtlasSprite back = uploader.getBackSprite();
        final Matrix4f model = stack.last().pose();
        final Matrix3f normal = stack.last().normal();
        final int blockWidth = width / 16;
        final int blockHeight = height / 16;
        final float offsetX = -blockWidth / 2.0F;
        final float offsetY = -blockHeight / 2.0F;
        final float depth = 0.5F / 16.0F;
        final float bu0 = back.getU0();
        final float bu1 = back.getU1();
        final float bv0 = back.getV0();
        final float bv1 = back.getV1();
        final float bup = back.getU(1.0D);
        final float bvp = back.getV(1.0D);
        final double uvX = 16.0D / blockWidth;
        final double uvY = 16.0D / blockHeight;
        for (int x = 0; x < blockWidth; ++x) {
            for (int y = 0; y < blockHeight; ++y) {
                final float x1 = offsetX + (x + 1);
                final float x0 = offsetX + x;
                final float y1 = offsetY + (y + 1);
                final float y0 = offsetY + y;
                final float u0 = art.getU(uvX * (blockWidth - x));
                final float u1 = art.getU(uvX * (blockWidth - x - 1));
                final float v0 = art.getV(uvY * (blockHeight - y));
                final float v1 = art.getV(uvY * (blockHeight - y - 1));
                this.vert(model, normal, buf, x1, y0, u1, v0, -depth, 0, 0, -1, packedLight);
                this.vert(model, normal, buf, x0, y0, u0, v0, -depth, 0, 0, -1, packedLight);
                this.vert(model, normal, buf, x0, y1, u0, v1, -depth, 0, 0, -1, packedLight);
                this.vert(model, normal, buf, x1, y1, u1, v1, -depth, 0, 0, -1, packedLight);
                this.vert(model, normal, buf, x1, y1, bu0, bv0, depth, 0, 0, 1, packedLight);
                this.vert(model, normal, buf, x0, y1, bu1, bv0, depth, 0, 0, 1, packedLight);
                this.vert(model, normal, buf, x0, y0, bu1, bv1, depth, 0, 0, 1, packedLight);
                this.vert(model, normal, buf, x1, y0, bu0, bv1, depth, 0, 0, 1, packedLight);
                this.vert(model, normal, buf, x1, y1, bu0, bv0, -depth, 0, 1, 0, packedLight);
                this.vert(model, normal, buf, x0, y1, bu1, bv0, -depth, 0, 1, 0, packedLight);
                this.vert(model, normal, buf, x0, y1, bu1, bvp, depth, 0, 1, 0, packedLight);
                this.vert(model, normal, buf, x1, y1, bu0, bvp, depth, 0, 1, 0, packedLight);
                this.vert(model, normal, buf, x1, y0, bu0, bv0, depth, 0, -1, 0, packedLight);
                this.vert(model, normal, buf, x0, y0, bu1, bv0, depth, 0, -1, 0, packedLight);
                this.vert(model, normal, buf, x0, y0, bu1, bvp, -depth, 0, -1, 0, packedLight);
                this.vert(model, normal, buf, x1, y0, bu0, bvp, -depth, 0, -1, 0, packedLight);
                this.vert(model, normal, buf, x1, y1, bup, bv0, depth, -1, 0, 0, packedLight);
                this.vert(model, normal, buf, x1, y0, bup, bv1, depth, -1, 0, 0, packedLight);
                this.vert(model, normal, buf, x1, y0, bu0, bv1, -depth, -1, 0, 0, packedLight);
                this.vert(model, normal, buf, x1, y1, bu0, bv0, -depth, -1, 0, 0, packedLight);
                this.vert(model, normal, buf, x0, y1, bup, bv0, -depth, 1, 0, 0, packedLight);
                this.vert(model, normal, buf, x0, y0, bup, bv1, -depth, 1, 0, 0, packedLight);
                this.vert(model, normal, buf, x0, y0, bu0, bv1, depth, 1, 0, 0, packedLight);
                this.vert(model, normal, buf, x0, y1, bu0, bv0, depth, 1, 0, 0, packedLight);
            }
        }
    }

    private void vert(final Matrix4f stack, final Matrix3f normal, final VertexConsumer buf, final float x, final float y, final float u, final float v, final float z, final int nx, final int ny, final int nz, final int packedLight) {
        buf.vertex(stack, x, y, z).color(0xFF, 0xFF, 0xFF, 0xFF).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, nx, ny, nz).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(final SupplyCartEntity entity) {
        return TEXTURE;
    }

    private enum Contents {
        FLOWERS(s -> s.getItem() instanceof BlockItem && s.is(ItemTags.FLOWERS), SupplyCartRenderer::renderFlowers),
        PAINTINGS(s -> s.getItem() == Items.PAINTING, SupplyCartRenderer::renderPaintings),
        WHEEL(s -> AstikorCarts.Items.WHEEL.filter(s.getItem()::equals).isPresent(), SupplyCartRenderer::renderWheel),
        SUPPLIES(s -> true, SupplyCartRenderer::renderSupplies);

        private final Predicate<? super ItemStack> predicate;
        private final ContentsRenderer renderer;

        Contents(final Predicate<? super ItemStack> predicate, final ContentsRenderer renderer) {
            this.predicate = predicate;
            this.renderer = renderer;
        }
    }

    @FunctionalInterface
    private interface ContentsRenderer {
        void render(final SupplyCartRenderer renderer, final SupplyCartEntity entity, final PoseStack stack, final MultiBufferSource source, final int packedLight, final NonNullList<ItemStack> cargo);
    }
}
