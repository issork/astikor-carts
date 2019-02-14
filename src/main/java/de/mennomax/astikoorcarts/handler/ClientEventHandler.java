package de.mennomax.astikoorcarts.handler;

import de.mennomax.astikoorcarts.entity.EntityCargoCart;
import de.mennomax.astikoorcarts.init.ModKeybindings;
import de.mennomax.astikoorcarts.packets.CPacketActionKey;
import de.mennomax.astikoorcarts.packets.CPacketOpenCartGui;
import de.mennomax.astikoorcarts.packets.CPacketToggleSlow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class ClientEventHandler
{

    @SubscribeEvent
    public void onClientTickEvent(ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END)
        {
            if (Minecraft.getMinecraft().world != null)
            {
                if (ModKeybindings.keybindings.get(0).isPressed())
                {
                    PacketHandler.INSTANCE.sendToServer(new CPacketActionKey());
                }
                if (Minecraft.getMinecraft().gameSettings.keyBindSprint.isPressed())
                {
                    PacketHandler.INSTANCE.sendToServer(new CPacketToggleSlow());
                }
            }
        }
    }
    
    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event)
    {
        if (event.getGui() instanceof GuiInventory)
        {
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            if (player.getRidingEntity() instanceof EntityCargoCart)
            {
                event.setCanceled(true);
                player.world.sendPacketToServer(PacketHandler.INSTANCE.getPacketFrom(new CPacketOpenCartGui(0, player.getRidingEntity().getEntityId())));
            }
        }
    }
}
