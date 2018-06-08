package astikoor.handler;

import astikoor.entity.EntityRiddenCart;
import astikoor.init.ModKeybindings;
import astikoor.packets.SPacketActionKey;
import astikoor.packets.SPacketMoveCart;
import astikoor.packets.SPacketRiddenSprint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientTickEventHandler
{
    private boolean oldstate = false;

    @SubscribeEvent
    public void onClientTickEvent(ClientTickEvent event)
    {
        if(Minecraft.getMinecraft().world == null)
        {
            return;
        }
        if(ModKeybindings.keybindings.get(0).isPressed())
        {
            PacketHandler.INSTANCE.sendToServer(new SPacketActionKey());
        }
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if(player.isRiding())
        {
            if(player.getRidingEntity() instanceof EntityRiddenCart)
            {
                if(Minecraft.getMinecraft().gameSettings.keyBindSprint.isPressed())
                {
                    PacketHandler.INSTANCE.sendToServer(new SPacketRiddenSprint());
                    ((EntityRiddenCart) player.getRidingEntity()).getPulling().setSprinting(true);
                }
                boolean newstate = Minecraft.getMinecraft().gameSettings.keyBindForward.isKeyDown();
                if(oldstate != newstate)
                {
                    oldstate = newstate;
                    PacketHandler.INSTANCE.sendToServer(new SPacketMoveCart(newstate));
                    ((EntityRiddenCart) player.getRidingEntity()).updateForward(newstate);
                }
            }
        }
        if(Minecraft.getMinecraft().gameSettings.keyBindSneak.isKeyDown())
        {
            if(oldstate)
            {
                oldstate = false;
            }
        }
    }
}
