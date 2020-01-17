package de.mennomax.astikorcarts;

import de.mennomax.astikorcarts.config.AstikorCartsConfig;
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
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

@Mod(AstikorCarts.MODID)
public class AstikorCarts {
    public static final String MODID = "astikorcarts";
    public static final HashMap<Entity, AbstractDrawnEntity> CLIENTPULLMAP = new HashMap<>();
    public static final HashMap<Entity, AbstractDrawnEntity> SERVERPULLMAP = new HashMap<>();

    public AstikorCarts() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, AstikorCartsConfig.COMMONSPEC);
    }

    @EventBusSubscriber(value = Dist.CLIENT)
    public static class ClientEventHandler {

        @SubscribeEvent
        public static void onInputUpdate(final InputUpdateEvent event) {
            final Minecraft mc = Minecraft.getInstance();
            final Entity ridden = mc.player.getRidingEntity();
            if (AstikorCarts.CLIENTPULLMAP.containsKey(ridden)) {
                final KeyBinding binding = mc.gameSettings.keyBindSprint;
                while (binding.isPressed()) {
                    PacketHandler.CHANNEL.sendToServer(new CPacketToggleSlow());
                    KeyBinding.setKeyBindState(binding.getKey(), false);
                }
            }
        }

        @SubscribeEvent
        public static void clientTickEvent(final ClientTickEvent event) {
            if (event.phase == Phase.END) {
                if (Minecraft.getInstance().world != null) {
                    while (KeyBindings.KEYBINDINGS[0].isPressed()) {
                        PacketHandler.CHANNEL.sendToServer(new CPacketActionKey());
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
                final ClientPlayerEntity player = Minecraft.getInstance().player;
                if (player.getRidingEntity() instanceof CargoCartEntity) {
                    event.setCanceled(true);
                    PacketHandler.CHANNEL.sendToServer(new CPacketOpenCargoCartGui(player.getRidingEntity().getEntityId()));
                }
            }
        }

    }

    @EventBusSubscriber
    public static class CommonEventHandler {
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
        public static void stopServer(final FMLServerStoppedEvent event) {
            CLIENTPULLMAP.clear();
            SERVERPULLMAP.clear();
        }
    }

    private static void tickPulled(final HashMap<Entity, AbstractDrawnEntity> pullmap) {
        final Iterator<Entry<Entity, AbstractDrawnEntity>> iter = pullmap.entrySet().iterator();
        while (iter.hasNext()) {
            final Entry<Entity, AbstractDrawnEntity> entry = iter.next();
            final AbstractDrawnEntity cart = entry.getValue();
            if (cart.shouldStopPulledTick()) {
                iter.remove();
                continue;
            }
            cart.pulledTick();
        }
    }
}
