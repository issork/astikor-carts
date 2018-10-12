package de.mennomax.horsecarts.handler;

import de.mennomax.horsecarts.AstikoorCarts;
import de.mennomax.horsecarts.entity.EntityCargoCart;
import de.mennomax.horsecarts.packets.CPacketOpenCartGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GuiEventHandler {
	
	@SubscribeEvent
	public void onGuiOpen(GuiOpenEvent event) {
		if(event.getGui() instanceof GuiInventory) {
			EntityPlayerSP player = Minecraft.getMinecraft().player;
			if(player.getRidingEntity() instanceof EntityCargoCart)
			{
				event.setCanceled(true);
				player.world.sendPacketToServer(PacketHandler.INSTANCE.getPacketFrom(new CPacketOpenCartGui(0, player.getRidingEntity().getEntityId())));
			}
		}
	}
		
}
	