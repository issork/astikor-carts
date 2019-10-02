package de.mennomax.astikorcarts;

import java.util.concurrent.ConcurrentHashMap;

import de.mennomax.astikorcarts.entity.AbstractDrawnEntity;
import de.mennomax.astikorcarts.entity.ai.goal.PullCartGoal;
import de.mennomax.astikorcarts.init.KeyBindings;
import de.mennomax.astikorcarts.network.PacketHandler;
import de.mennomax.astikorcarts.network.packets.CPacketActionKey;
import de.mennomax.astikorcarts.network.packets.CPacketToggleSlow;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.MobEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;

@Mod(AstikorCarts.MODID)
public class AstikorCarts {
    public static final String MODID = "astikorcarts";
    //TODO: Replace with better system
    public static final ConcurrentHashMap<Integer, Integer> PULLMAP = new ConcurrentHashMap<>();

    @EventBusSubscriber(value = Dist.CLIENT)
    public static class clientEventHandler {
        
        @SubscribeEvent
        public static void clientTickEvent(final ClientTickEvent event) {
            if (event.phase == Phase.END) {
                if (Minecraft.getInstance().world != null) {
                    while (KeyBindings.KEYBINDINGS[0].isPressed()) {
                        PacketHandler.channel.sendToServer(new CPacketActionKey());
                    }
                    while (Minecraft.getInstance().gameSettings.keyBindSprint.isPressed()) {
                        PacketHandler.channel.sendToServer(new CPacketToggleSlow());
                    }
                }
                for(Integer entityId : PULLMAP.values()) {
                    AbstractDrawnEntity entity = (AbstractDrawnEntity) Minecraft.getInstance().world.getEntityByID(entityId);
                    if(entity != null) {
                        entity.pulledTick();
                    }
                }
            }
        }
        
        @SubscribeEvent
        public static void stopServer(final FMLServerStoppedEvent event) {
            PULLMAP.clear();
        }
    }

    @EventBusSubscriber
    public static class commonEventHandler {
        @SubscribeEvent
        public static void joinWorld(final EntityJoinWorldEvent event) {
            if (!event.getWorld().isRemote && event.getEntity() instanceof MobEntity) {
                ((MobEntity) event.getEntity()).goalSelector.addGoal(1, new PullCartGoal(event.getEntity()));
            }
        }
        
        @SubscribeEvent
        public static void worldTick(final WorldTickEvent event) {
            if(event.phase == Phase.END) {
                for(Integer entityId : PULLMAP.values()) {
                    AbstractDrawnEntity entity = (AbstractDrawnEntity) event.world.getEntityByID(entityId);
                    if(entity != null) {
                        entity.pulledTick();
                    }
                }
            }
        }
    }
}
