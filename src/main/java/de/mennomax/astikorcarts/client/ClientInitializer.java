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
import de.mennomax.astikorcarts.entity.MobCartEntity;
import de.mennomax.astikorcarts.entity.PlowCartEntity;
import de.mennomax.astikorcarts.entity.PostilionEntity;
import de.mennomax.astikorcarts.network.PacketHandler;
import de.mennomax.astikorcarts.network.packets.CPacketActionKey;
import de.mennomax.astikorcarts.network.packets.CPacketToggleSlow;
import de.mennomax.astikorcarts.world.AstikorWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

public class ClientInitializer extends CommonInitializer {
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
                        PacketHandler.CHANNEL.sendToServer(new CPacketActionKey());
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
                final Entity ridden = player.getRidingEntity();
                if (ridden != null && AstikorWorld.get(ridden.world).map(w -> w.isPulling(ridden)).orElse(false)) {
                    final KeyBinding binding = mc.gameSettings.keyBindSprint;
                    while (binding.isPressed()) {
                        PacketHandler.CHANNEL.sendToServer(new CPacketToggleSlow());
                        KeyBinding.setKeyBindState(binding.getKey(), false);
                    }
                }
            }
        });
        mod.modBus().<FMLClientSetupEvent>addListener(e -> {
            RenderingRegistry.registerEntityRenderingHandler(CargoCartEntity.class, CargoCartRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(PlowCartEntity.class, PlowCartRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(MobCartEntity.class, MobCartRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(PostilionEntity.class, PostilionRenderer::new);
            ScreenManager.registerFactory(AstikorCarts.ContainerTypes.PLOWCARTCONTAINER.get(), PlowScreen::new);
            ClientRegistry.registerKeyBinding(this.action);
        });
    }
}
