package de.mennomax.astikorcarts.client;

import de.mennomax.astikorcarts.AstikorCarts;
import de.mennomax.astikorcarts.CommonInitializer;
import de.mennomax.astikorcarts.client.gui.screen.inventory.PlowScreen;
import de.mennomax.astikorcarts.client.oregon.OregonSubscriber;
import de.mennomax.astikorcarts.client.renderer.entity.CargoCartRenderer;
import de.mennomax.astikorcarts.client.renderer.entity.MobCartRenderer;
import de.mennomax.astikorcarts.client.renderer.entity.PlowCartRenderer;
import de.mennomax.astikorcarts.client.renderer.entity.PostilionRenderer;
import de.mennomax.astikorcarts.entity.CargoCartEntity;
import de.mennomax.astikorcarts.network.packets.CPacketActionKey;
import de.mennomax.astikorcarts.network.packets.CPacketOpenCargoCartGui;
import de.mennomax.astikorcarts.network.packets.CPacketToggleSlow;
import de.mennomax.astikorcarts.world.AstikorWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

public final class ClientInitializer extends CommonInitializer {
    private final KeyBinding action = new KeyBinding("key.astikorcarts.desc", GLFW.GLFW_KEY_R, "key.categories.astikorcarts");

    @Override
    public void init(final Context mod) {
        super.init(mod);
        mod.bus().register(new OregonSubscriber());
        mod.bus().<TickEvent.ClientTickEvent>addListener(e -> {
            if (e.phase == TickEvent.Phase.END) {
                final Minecraft mc = Minecraft.getInstance();
                final World world = mc.world;
                if (world != null) {
                    while (this.action.isPressed()) {
                        AstikorCarts.CHANNEL.sendToServer(new CPacketActionKey());
                    }
                    if (!mc.isGamePaused()) {
                        AstikorWorld.get(world).ifPresent(AstikorWorld::tick);
                    }
                }
            }
        });
        mod.bus().<InputEvent.KeyInputEvent>addListener(e -> {
            final Minecraft mc = Minecraft.getInstance();
            final PlayerEntity player = mc.player;
            if (player != null) {
                if (CPacketToggleSlow.getPulling(player) != null) {
                    final KeyBinding binding = mc.gameSettings.keyBindSprint;
                    while (binding.isPressed()) {
                        AstikorCarts.CHANNEL.sendToServer(new CPacketToggleSlow());
                        KeyBinding.setKeyBindState(binding.getKey(), false);
                    }
                }
            }
        });
        mod.bus().<GuiOpenEvent>addListener(e -> {
            if (e.getGui() instanceof InventoryScreen) {
                final ClientPlayerEntity player = Minecraft.getInstance().player;
                if (player.getRidingEntity() instanceof CargoCartEntity) {
                    e.setCanceled(true);
                    AstikorCarts.CHANNEL.sendToServer(new CPacketOpenCargoCartGui());
                }
            }
        });
        mod.modBus().<FMLClientSetupEvent>addListener(e -> {
            RenderingRegistry.registerEntityRenderingHandler(AstikorCarts.EntityTypes.CARGO_CART.get(), CargoCartRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(AstikorCarts.EntityTypes.PLOW_CART.get(), PlowCartRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(AstikorCarts.EntityTypes.MOB_CART.get(), MobCartRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(AstikorCarts.EntityTypes.POSTILION.get(), PostilionRenderer::new);
            ScreenManager.registerFactory(AstikorCarts.ContainerTypes.PLOW_CART.get(), PlowScreen::new);
            ClientRegistry.registerKeyBinding(this.action);
        });
    }
}
