package de.mennomax.astikorcarts.client;

import de.mennomax.astikorcarts.AstikorCarts;
import de.mennomax.astikorcarts.CommonInitializer;
import de.mennomax.astikorcarts.client.gui.screen.inventory.PlowScreen;
import de.mennomax.astikorcarts.client.oregon.OregonSubscriber;
import de.mennomax.astikorcarts.client.renderer.entity.SupplyCartRenderer;
import de.mennomax.astikorcarts.client.renderer.entity.AnimalCartRenderer;
import de.mennomax.astikorcarts.client.renderer.entity.PlowRenderer;
import de.mennomax.astikorcarts.client.renderer.entity.PostilionRenderer;
import de.mennomax.astikorcarts.client.renderer.texture.AssembledTexture;
import de.mennomax.astikorcarts.client.renderer.texture.AssembledTextureFactory;
import de.mennomax.astikorcarts.client.renderer.texture.Material;
import de.mennomax.astikorcarts.entity.SupplyCartEntity;
import de.mennomax.astikorcarts.network.serverbound.ActionKeyMessage;
import de.mennomax.astikorcarts.network.serverbound.OpenSupplyCartMessage;
import de.mennomax.astikorcarts.network.serverbound.ToggleSlowMessage;
import de.mennomax.astikorcarts.world.AstikorWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
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
        new OregonSubscriber().register(mod.bus());
        mod.bus().<TickEvent.ClientTickEvent>addListener(e -> {
            if (e.phase == TickEvent.Phase.END) {
                final Minecraft mc = Minecraft.getInstance();
                final World world = mc.world;
                if (world != null) {
                    while (this.action.isPressed()) {
                        AstikorCarts.CHANNEL.sendToServer(new ActionKeyMessage());
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
                if (ToggleSlowMessage.getCart(player).isPresent()) {
                    final KeyBinding binding = mc.gameSettings.keyBindSprint;
                    while (binding.isPressed()) {
                        AstikorCarts.CHANNEL.sendToServer(new ToggleSlowMessage());
                        KeyBinding.setKeyBindState(binding.getKey(), false);
                    }
                }
            }
        });
        mod.bus().<GuiOpenEvent>addListener(e -> {
            if (e.getGui() instanceof InventoryScreen) {
                final ClientPlayerEntity player = Minecraft.getInstance().player;
                if (player != null && player.getRidingEntity() instanceof SupplyCartEntity) {
                    e.setCanceled(true);
                    AstikorCarts.CHANNEL.sendToServer(new OpenSupplyCartMessage());
                }
            }
        });
        mod.modBus().<FMLClientSetupEvent>addListener(e -> {
            RenderingRegistry.registerEntityRenderingHandler(AstikorCarts.EntityTypes.SUPPLY_CART.get(), SupplyCartRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(AstikorCarts.EntityTypes.PLOW.get(), PlowRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(AstikorCarts.EntityTypes.ANIMAL_CART.get(), AnimalCartRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(AstikorCarts.EntityTypes.POSTILION.get(), PostilionRenderer::new);
            ScreenManager.registerFactory(AstikorCarts.ContainerTypes.PLOW_CART.get(), PlowScreen::new);
            ClientRegistry.registerKeyBinding(this.action);
        });
        new AssembledTextureFactory()
            .add(new ResourceLocation(AstikorCarts.ID, "textures/entity/animal_cart.png"), new AssembledTexture(64, 64)
                .add(new Material(new ResourceLocation("block/oak_planks"), 16)
                    .fill(0, 0, 60, 38, Material.R0, 0, 2)
                    .fill(0, 28, 20, 33, Material.R90, 4, -2)
                    .fill(12, 30, 8, 31, Material.R270, 0, 4)
                )
                .add(new Material(new ResourceLocation("block/stripped_spruce_log"), 16)
                    .fill(54, 54, 10, 10, Material.R0, 0, 2)
                )
                .add(new Material(new ResourceLocation("block/oak_log"), 16)
                    .fill(0, 21, 60, 4, Material.R90)
                    .fill(46, 60, 8, 4, Material.R90)
                )
                .add(new Material(new ResourceLocation("block/stone"), 16)
                    .fill(62, 55, 2, 9)
                )
            )
            .add(new ResourceLocation(AstikorCarts.ID, "textures/entity/plow.png"), new AssembledTexture(64, 64)
                .add(new Material(new ResourceLocation("block/oak_planks"), 16)
                    .fill(0, 0, 64, 32, Material.R90)
                    .fill(0, 8, 42, 3, Material.R0, 0, 1)
                    .fill(0, 27, 34, 3, Material.R0, 0, 2)
                )
                .add(new Material(new ResourceLocation("block/stripped_spruce_log"), 16)
                    .fill(54, 54, 10, 10, Material.R0, 2, 0)
                )
                .add(new Material(new ResourceLocation("block/oak_log"), 16)
                    .fill(0, 0, 54, 4, Material.R90)
                    .fill(46, 60, 8, 4, Material.R90)
                )
                .add(new Material(new ResourceLocation("block/stone"), 16)
                    .fill(62, 55, 2, 9)
                )
            )
            .add(new ResourceLocation(AstikorCarts.ID, "textures/entity/supply_cart.png"), new AssembledTexture(64, 64)
                .add(new Material(new ResourceLocation("block/oak_planks"), 16)
                    .fill(0, 0, 60, 45, Material.R0, 0, 2)
                    .fill(0, 27, 60, 8, Material.R0, 0, 1)
                )
                .add(new Material(new ResourceLocation("block/stripped_spruce_log"), 16)
                    .fill(54, 54, 10, 10, Material.R0, 0, 2)
                )
                .add(new Material(new ResourceLocation("block/oak_log"), 16)
                    .fill(0, 23, 54, 4, Material.R90)
                    .fill(46, 60, 8, 4, Material.R90)
                )
                .add(new Material(new ResourceLocation("block/stone"), 16)
                    .fill(62, 55, 2, 9)
                )
                .add(new Material(new ResourceLocation("block/composter_side"), 16)
                    .fill(16, 47, 44, 5, Material.R0, -2, 1)
                    .fill(16, 54, 38, 5, Material.R0, -2, -6)
                )
                .add(new Material(new ResourceLocation("block/composter_top"), 16)
                    .fill(18, 45, 10, 2, Material.R0, -2, 3)
                    .fill(28, 45, 10, 2, Material.R0, 10, 3)
                    .fill(18, 52, 8, 2, Material.R0, 0, -4)
                    .fill(26, 52, 9, 2, Material.R0, 11, -4)
                )
                .add(new Material(new ResourceLocation("block/dirt"), 16)
                    .fill(0, 45, 16, 17)
                )
            )
            .register(mod.modBus());
    }
}
