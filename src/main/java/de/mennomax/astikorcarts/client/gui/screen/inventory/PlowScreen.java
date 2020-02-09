package de.mennomax.astikorcarts.client.gui.screen.inventory;

import com.mojang.blaze3d.platform.GlStateManager;
import de.mennomax.astikorcarts.AstikorCarts;
import de.mennomax.astikorcarts.inventory.container.CartContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class PlowScreen extends ContainerScreen<CartContainer> {

    private static final ResourceLocation PLOW_GUI_TEXTURES = new ResourceLocation(AstikorCarts.ID, "textures/gui/container/plow.png");

    public PlowScreen(final CartContainer screenContainer, final PlayerInventory inv, final ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(final int mouseX, final int mouseY) {
        this.font.drawString(this.title.getFormattedText(), 8.0F, 6.0F, 4210752);
        this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(PLOW_GUI_TEXTURES);
        final int i = (this.width - this.xSize) / 2;
        final int j = (this.height - this.ySize) / 2;
        this.blit(i, j, 0, 0, this.xSize, this.ySize);
    }

    @Override
    public void render(final int mouseX, final int mouseY, final float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

}
