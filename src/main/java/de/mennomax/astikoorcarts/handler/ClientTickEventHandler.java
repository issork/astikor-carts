package de.mennomax.astikoorcarts.handler;

import de.mennomax.astikoorcarts.entity.AbstractRiddenDrawn;
import de.mennomax.astikoorcarts.init.ModKeybindings;
import de.mennomax.astikoorcarts.packets.CPacketActionKey;
import de.mennomax.astikoorcarts.packets.CPacketMoveCart;
import de.mennomax.astikoorcarts.packets.CPacketRiddenSprint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class ClientTickEventHandler
{
    private boolean oldstate = false;

    @SubscribeEvent
    public void onClientTickEvent(ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END)
        {
            if (Minecraft.getMinecraft().world == null)
            {
                return;
            }
            if (ModKeybindings.keybindings.get(0).isPressed())
            {
                PacketHandler.INSTANCE.sendToServer(new CPacketActionKey());
            }
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            if (player.isRiding())
            {
                if (player.getRidingEntity() instanceof AbstractRiddenDrawn)
                {
                    AbstractRiddenDrawn cart = (AbstractRiddenDrawn) player.getRidingEntity();
                    if (cart.getPulling() != null)
                    {
                        if (Minecraft.getMinecraft().gameSettings.keyBindSprint.isPressed())
                        {
                            PacketHandler.INSTANCE.sendToServer(new CPacketRiddenSprint());
                            cart.getPulling().setSprinting(true);
                        }
                        boolean newstate = Minecraft.getMinecraft().gameSettings.keyBindForward.isKeyDown();
                        if (oldstate != newstate)
                        {
                            oldstate = newstate;
                            PacketHandler.INSTANCE.sendToServer(new CPacketMoveCart(newstate));
                            cart.updateForward(newstate);
                        }
                    }
                }
            }
            if (Minecraft.getMinecraft().gameSettings.keyBindSneak.isKeyDown())
            {
                if (oldstate)
                {
                    oldstate = false;
                }
            }
        }
    }
}
