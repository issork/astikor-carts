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
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.list.AbstractList;
import net.minecraft.stats.Stat;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public final class OregonSubscriber {
    private static final Logger LOGGER = LogManager.getLogger();

    private State state = new IdleState();

    @SubscribeEvent
    public void onScreenKeyPressed(final GuiScreenEvent.KeyboardKeyPressedEvent.Pre event) {
        final Minecraft mc = Minecraft.getInstance();
        final Screen screen = event.getGui();
        if (screen instanceof StatsScreen && (event.getKeyCode() == GLFW.GLFW_KEY_ENTER || event.getKeyCode() == GLFW.GLFW_KEY_KP_ENTER) && mc.player != null) {
            this.getSelectedStat((StatsScreen) screen).ifPresent(stat -> {
                if (AstikorStats.CART_ONE_CM.equals(stat.getValue()) && mc.player.getStats().getValue(stat) > 2040 * 100) {
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
            });
        }
        if (screen instanceof ChatScreen && (event.getKeyCode() == GLFW.GLFW_KEY_ENTER || event.getKeyCode() == GLFW.GLFW_KEY_KP_ENTER)) {
            screen.children().stream()
                .filter(TextFieldWidget.class::isInstance)
                .map(TextFieldWidget.class::cast)
                .findFirst().ifPresent(field -> {
                    if (this.state.onChat(field)) {
                        event.setCanceled(true);
                    }
                });
        }
    }

    private Optional<Stat<?>> getSelectedStat(final StatsScreen screen) {
        final AbstractList<?> list = screen.func_213116_d();
        if (list == null) {
            return Optional.empty();
        }
        final Class<?> classCustomStatsList$Entry;
        try {
            classCustomStatsList$Entry = Class.forName("net.minecraft.client.gui.screen.StatsScreen$CustomStatsList$Entry");
        } catch (final ClassNotFoundException e) {
            LOGGER.error("Unable to lookup custom stat entry class", e);
            return Optional.empty();
        }
        final AbstractList.AbstractListEntry<?> entry = list.getSelected();
        if (!classCustomStatsList$Entry.isInstance(entry)) {
            return Optional.empty();
        }
        final String statFieldName = ObfuscationReflectionHelper.remapName(INameMappingService.Domain.FIELD, "field_214405_b");
        final Stat<?> stat;
        try {
            final Field fieldStat = classCustomStatsList$Entry.getDeclaredField(statFieldName);
            fieldStat.setAccessible(true);
            stat = (Stat<?>) fieldStat.get(entry);
        } catch (final NoSuchFieldException | IllegalAccessException e) {
            LOGGER.error("Unable to retrieve stat from entry {}", entry.getClass(), e);
            return Optional.empty();
        }
        return Optional.of(stat);
    }

    private void setState(final State newState) {
        this.state.stop();
        this.state = newState;
        this.state.start();
    }

    abstract static class State {
        abstract void start();

        abstract void stop();

        abstract boolean onChat(final TextFieldWidget field);
    }

    static class IdleState extends State {
        @Override
        void start() {
        }

        @Override
        void stop() {
        }

        @Override
        public boolean onChat(final TextFieldWidget field) {
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
            Minecraft.getInstance().displayGuiScreen(new ChatScreen("? "));
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
        public boolean onChat(final TextFieldWidget field) {
            final String text = field.getText();
            if (text.startsWith("?")) {
                this.io.add(text.substring(1).trim());
                field.setText("? ");
                return true;
            }
            return false;
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
            mc.runImmediately(() -> {
                final ClientPlayerEntity player = mc.player;
                if (player != null) {
                    player.sendMessage(new StringTextComponent(s).setStyle(new Style().setItalic(true).setColor(TextFormatting.GRAY)));
                }
            });
        }
    }
}
