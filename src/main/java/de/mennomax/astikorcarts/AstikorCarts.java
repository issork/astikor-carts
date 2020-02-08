package de.mennomax.astikorcarts;

import de.mennomax.astikorcarts.client.oregon.OregonSubscriber;
import de.mennomax.astikorcarts.config.AstikorCartsConfig;
import de.mennomax.astikorcarts.entity.CargoCartEntity;
import de.mennomax.astikorcarts.entity.PostilionEntity;
import de.mennomax.astikorcarts.entity.ai.goal.PullCartGoal;
import de.mennomax.astikorcarts.init.KeyBindings;
import de.mennomax.astikorcarts.network.PacketHandler;
import de.mennomax.astikorcarts.network.packets.CPacketActionKey;
import de.mennomax.astikorcarts.network.packets.CPacketOpenCargoCartGui;
import de.mennomax.astikorcarts.network.packets.CPacketToggleSlow;
import de.mennomax.astikorcarts.world.AstikorWorld;
import de.mennomax.astikorcarts.world.SimpleAstikorWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import javax.annotation.Nullable;

@Mod(AstikorCarts.MODID)
public class AstikorCarts {
    public static final String MODID = "astikorcarts";

    public AstikorCarts() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, AstikorCartsConfig.COMMONSPEC);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> MinecraftForge.EVENT_BUS.register(new OregonSubscriber()));
        FMLJavaModLoadingContext.get().getModEventBus().<FMLCommonSetupEvent>addListener(e -> {
            CapabilityManager.INSTANCE.register(AstikorWorld.class, new Capability.IStorage<AstikorWorld>() {
                @Nullable
                @Override
                public INBT writeNBT(final Capability<AstikorWorld> capability, final AstikorWorld instance, final Direction side) {
                    return null;
                }

                @Override
                public void readNBT(final Capability<AstikorWorld> capability, final AstikorWorld instance, final Direction side, final INBT nbt) {
                }
            }, SimpleAstikorWorld::new);
        });
    }

    @EventBusSubscriber(value = Dist.CLIENT)
    public static class ClientEventHandler {

        @SubscribeEvent
        public static void onInputUpdate(final InputEvent.KeyInputEvent event) {
            final Minecraft mc = Minecraft.getInstance();
            final Entity ridden = mc.player.getRidingEntity();
            if (ridden != null && AstikorWorld.get(ridden.world).map(w -> w.isPulling(ridden)).orElse(false)) {
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
                final Minecraft mc = Minecraft.getInstance();
                final World world = mc.world;
                if (world != null) {
                    while (KeyBindings.ACTION.isPressed()) {
                        PacketHandler.CHANNEL.sendToServer(new CPacketActionKey());
                    }
                    if (!mc.isGamePaused()) {
                        AstikorWorld.get(world).ifPresent(AstikorWorld::tick);
                    }
                }
            }
        }

        @SubscribeEvent
        public static void openGui(final GuiOpenEvent event) {
            if (event.getGui() instanceof InventoryScreen) {
                final ClientPlayerEntity player = Minecraft.getInstance().player;
                if (player.getRidingEntity() instanceof CargoCartEntity) {
                    event.setCanceled(true);
                    PacketHandler.CHANNEL.sendToServer(new CPacketOpenCargoCartGui());
                }
            }
        }
    }

    @EventBusSubscriber
    public static class CommonEventHandler {
        @SubscribeEvent
        public static void onAttachCapabilities(final AttachCapabilitiesEvent<World> event) {
            event.addCapability(new ResourceLocation(MODID, "astikor"), new ICapabilityProvider() {
                private final LazyOptional<SimpleAstikorWorld> instance = LazyOptional.of(SimpleAstikorWorld::new);

                @Override
                public <T> LazyOptional<T> getCapability(final Capability<T> cap, @Nullable final Direction side) {
                    return cap == AstikorWorld.capability() ? this.instance.cast() : LazyOptional.empty();
                }
            });
        }

        @SubscribeEvent
        public static void joinWorld(final EntityJoinWorldEvent event) {
            if (!event.getWorld().isRemote && event.getEntity() instanceof MobEntity) {
                ((MobEntity) event.getEntity()).goalSelector.addGoal(1, new PullCartGoal(event.getEntity()));
            }
        }

        @SubscribeEvent
        public static void onInteract(final PlayerInteractEvent.EntityInteract event) {
            final Entity rider = event.getTarget().getControllingPassenger();
            if (rider instanceof PostilionEntity) {
                rider.stopRiding();
            }
        }

        @SubscribeEvent
        public static void worldTick(final WorldTickEvent event) {
            if (event.phase == Phase.END) {
                AstikorWorld.get(event.world).ifPresent(AstikorWorld::tick);
            }
        }
    }
}
