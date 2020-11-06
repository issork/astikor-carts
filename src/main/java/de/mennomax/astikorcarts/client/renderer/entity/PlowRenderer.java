package de.mennomax.astikorcarts.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.mennomax.astikorcarts.AstikorCarts;
import de.mennomax.astikorcarts.client.renderer.entity.model.PlowModel;
import de.mennomax.astikorcarts.entity.PlowEntity;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.function.Consumer;

public final class PlowRenderer extends DrawnRenderer<PlowEntity, PlowModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AstikorCarts.ID, "textures/entity/plow.png");

    public PlowRenderer(final EntityRendererManager renderManager) {
        super(renderManager, new PlowModel());
        this.shadowSize = 1.0F;
    }

    @Override
    public ResourceLocation getEntityTexture(final PlowEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void renderContents(final PlowEntity entity, final float delta, final MatrixStack stack, final IRenderTypeBuffer source, final int packedLight) {
        super.renderContents(entity, delta, stack, source, packedLight);
        for (int i = 0; i < entity.inventory.getSlots(); i++) {
            final ItemStack itemStack = entity.getStackInSlot(i);
            if (itemStack.isEmpty()) {
                continue;
            }
            this.attach(this.model.getBody(), this.model.getShaft(i), s -> {
                s.rotate(Vector3f.XP.rotationDegrees(-90.0F));
                s.rotate(Vector3f.YP.rotationDegrees(90.0F));
                s.translate(-4.0D / 16.0D, 1.0D / 16.0D, 0.0D);
                if (itemStack.getItem() instanceof BlockItem) {
                    s.translate(0.0D, -0.1D, 0.0D);
                    s.rotate(Vector3f.ZP.rotationDegrees(180.0F));
                }
                Minecraft.getInstance().getItemRenderer().renderItem(itemStack, ItemCameraTransforms.TransformType.FIXED, packedLight, OverlayTexture.NO_OVERLAY, s, source);
            }, stack);
        }
    }
}
