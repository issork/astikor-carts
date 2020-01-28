package de.mennomax.astikorcarts;

import cpw.mods.modlauncher.api.INameMappingService;
import de.mennomax.astikorcarts.init.AstikorStats;
import de.mennomax.astikorcarts.oregon.BasicProgram;
import de.mennomax.astikorcarts.oregon.Oregon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.StatsScreen;
import net.minecraft.client.gui.widget.list.AbstractList;
import net.minecraft.stats.Stat;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;
import java.util.Random;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public final class OregonSubscriber {
    private State state = new IdleState();

    @SubscribeEvent
    public void onScreenKeyPressed(final GuiScreenEvent.KeyboardKeyPressedEvent.Pre event) {
        final Minecraft mc = Minecraft.getInstance();
        final Screen screen = event.getGui();
        if (screen instanceof StatsScreen && (event.getKeyCode() == GLFW.GLFW_KEY_ENTER || event.getKeyCode() == GLFW.GLFW_KEY_KP_ENTER) && mc.player != null) {
            final AbstractList<?> list = ObfuscationReflectionHelper.getPrivateValue(StatsScreen.class, (StatsScreen) screen, "field_146550_h");
            if (list != null) {
                final Class<?> classCustomStatsList$Entry;
                try {
                    classCustomStatsList$Entry = Class.forName("net.minecraft.client.gui.screen.StatsScreen$CustomStatsList$Entry");
                } catch (final ClassNotFoundException e) {
                    return;
                }
                final AbstractList.AbstractListEntry<?> entry = list.getSelected();
                if (classCustomStatsList$Entry.isInstance(entry)) {
                    final Stat<?> stat;
                    try {
                        final String statFieldName = ObfuscationReflectionHelper.remapName(INameMappingService.Domain.FIELD, "field_214405_b");
                        final Field f = classCustomStatsList$Entry.getDeclaredField(statFieldName);
                        f.setAccessible(true);
                        stat = (Stat<?>) f.get(entry);
                    } catch (final NoSuchFieldException | IllegalAccessException e) {
                        return;
                    }
                    if (AstikorStats.CART_ONE_CM.equals(stat.getValue()) && mc.player.getStats().getValue(stat) > 2040 * 100) {
                        mc.displayGuiScreen(null);
                        final PlayerIO io = new PlayerIO();
                        final Oregon oregon = new Oregon(io, new Random());
                        this.setState(new ActiveState(new Thread(() -> {
                            try {
                                oregon.run();
                            } finally {
                                mc.enqueue(() -> this.setState(new IdleState()));
                            }
                        }, "Oregon Trail 1978"), io));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onChat(final ClientChatEvent event) {
        if (this.state.onChat(event.getMessage())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onScreenOpen(final GuiOpenEvent event) {
        if (this.state.onScreenOpen(event.getGui())) {
            event.setGui(new ChatScreen("? "));
        }
    }

    private void setState(final State newState) {
        final State oldState = this.state;
        this.state = newState;
        oldState.stop();
        newState.start();
    }

    abstract static class State {
        abstract void start();

        abstract void stop();

        abstract boolean onChat(final String message);

        public abstract boolean onScreenOpen(final Screen screen);
    }

    static class IdleState extends State {
        @Override
        void start() {
        }

        @Override
        void stop() {
        }

        @Override
        public boolean onChat(final String message) {
            return false;
        }

        @Override
        public boolean onScreenOpen(final Screen screen) {
            return false;
        }
    }

    static class ActiveState extends State {
        final Thread thread;
        final PlayerIO io;

        ActiveState(final Thread thread, final PlayerIO io) {
            this.thread = thread;
            this.io = io;
        }

        @Override
        void start() {
            this.thread.setDaemon(true);
            this.thread.start();
        }

        @Override
        void stop() {
            this.thread.interrupt();
            try {
                this.thread.join();
            } catch (final InterruptedException ignored) {
            }
            final Minecraft mc = Minecraft.getInstance();
            if (mc.currentScreen instanceof ChatScreen) {
                mc.displayGuiScreen(null);
            }
        }

        @Override
        public boolean onChat(final String message) {
            if (message.startsWith("?")) {
                this.io.add(message.substring(1).trim());
                return true;
            }
            return false;
        }

        @Override
        public boolean onScreenOpen(final Screen screen) {
            return screen == null && Minecraft.getInstance().currentScreen instanceof ChatScreen;
        }
    }

    static class PlayerIO implements BasicProgram.IO {
        final BlockingDeque<String> in = new LinkedBlockingDeque<>();

        void add(final String s) {
            this.in.addLast(s);
            final ClientPlayerEntity player = Minecraft.getInstance().player;
            if (player != null) {
                player.sendMessage(new StringTextComponent(s).setStyle(new Style().setItalic(true).setColor(TextFormatting.WHITE)));
            }
        }

        @Override
        public int prompt(final int lower, final int upper) throws InterruptedException {
            try {
                return Integer.parseInt(this.in.takeFirst());
            } catch (final NumberFormatException e) {
                return -1;
            }
        }

        @Override
        public String prompt(final String... options) throws InterruptedException {
            return this.in.takeFirst();
        }

        @Override
        public void print(final String s) {
            final Minecraft mc = Minecraft.getInstance();
            mc.enqueue(() -> {
                final ClientPlayerEntity player = mc.player;
                if (player != null) {
                    player.sendMessage(new StringTextComponent(s).setStyle(new Style().setItalic(true).setColor(TextFormatting.GRAY)));
                }
            });
        }
    }
}
