package de.mennomax.astikorcarts;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import de.mennomax.astikorcarts.entity.AbstractDrawnEntity;
import de.mennomax.astikorcarts.entity.CargoCartEntity;
import de.mennomax.astikorcarts.entity.ai.goal.PullCartGoal;
import de.mennomax.astikorcarts.init.KeyBindings;
import de.mennomax.astikorcarts.network.PacketHandler;
import de.mennomax.astikorcarts.network.packets.CPacketActionKey;
import de.mennomax.astikorcarts.network.packets.CPacketOpenCargoCartGui;
import de.mennomax.astikorcarts.network.packets.CPacketToggleSlow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;

@Mod(AstikorCarts.MODID)
public class AstikorCarts {
    public static final String MODID = "astikorcarts";
    public static final HashMap<Entity, AbstractDrawnEntity> CLIENTPULLMAP = new HashMap<>();
    public static final HashMap<Entity, AbstractDrawnEntity> SERVERPULLMAP = new HashMap<>();

    @EventBusSubscriber(value = Dist.CLIENT)
    public static class clientEventHandler {

        @SubscribeEvent
        public static void clientTickEvent(final ClientTickEvent event) {
            if (event.phase == Phase.END) {
                if (Minecraft.getInstance().world != null) {
                    while (KeyBindings.KEYBINDINGS[0].isPressed()) {
                        PacketHandler.CHANNEL.sendToServer(new CPacketActionKey());
                    }
                    while (Minecraft.getInstance().gameSettings.keyBindSprint.isPressed()) {
                        PacketHandler.CHANNEL.sendToServer(new CPacketToggleSlow());
                    }
                }
                if (!Minecraft.getInstance().isGamePaused()) {
                    tickPulled(CLIENTPULLMAP);
                }
            }
        }
        
        @SubscribeEvent
        public static void openGui(final GuiOpenEvent event) {
            if (event.getGui() instanceof InventoryScreen) {
                ClientPlayerEntity player = Minecraft.getInstance().player;
                if(player.getRidingEntity() instanceof CargoCartEntity) {
                    event.setCanceled(true);
                    PacketHandler.CHANNEL.sendToServer(new CPacketOpenCargoCartGui(player.getRidingEntity().getEntityId()));
                }
            }
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
            if (event.phase == Phase.END) {
                tickPulled(SERVERPULLMAP);
            }
        }

        @SubscribeEvent
        public static void stopServer(final FMLServerStoppingEvent event) {
            CLIENTPULLMAP.clear();
            SERVERPULLMAP.clear();
        }
    }

    private static void tickPulled(HashMap<Entity, AbstractDrawnEntity> pullmap) {
        Iterator<Entry<Entity, AbstractDrawnEntity>> iter = pullmap.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<Entity, AbstractDrawnEntity> entry = iter.next();
            AbstractDrawnEntity cart = entry.getValue();
            if (!cart.isAlive() || cart.getPulling() == null || !cart.getPulling().isAlive()) {
                if (entry.getKey() instanceof PlayerEntity) {
                    cart.setPulling(null);
                }
                iter.remove();
                continue;
            }
            cart.pulledTick();
        }
    }
}
