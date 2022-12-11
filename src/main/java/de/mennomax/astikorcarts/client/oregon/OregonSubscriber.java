package de.mennomax.astikorcarts.client.oregon;

import cpw.mods.modlauncher.api.INameMappingService;
import de.mennomax.astikorcarts.AstikorCarts;
import de.mennomax.astikorcarts.oregon.BasicProgram;
import de.mennomax.astikorcarts.oregon.Oregon;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.stats.Stat;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public final class OregonSubscriber {
    private static final Logger LOGGER = LogManager.getLogger();

    private State state = new IdleState();

    public void register(final IEventBus bus) {
        bus.addListener(this::onScreenKeyPressed);
    }

    private void onScreenKeyPressed(final ScreenEvent.KeyboardKeyPressedEvent.Pre event) {
        final Minecraft mc = Minecraft.getInstance();
        final Screen screen = event.getScreen();
        if (screen instanceof StatsScreen && (event.getKeyCode() == GLFW.GLFW_KEY_ENTER || event.getKeyCode() == GLFW.GLFW_KEY_KP_ENTER) && mc.player != null) {
            this.getSelectedStat((StatsScreen) screen).ifPresent(stat -> {
                if (AstikorCarts.Stats.CART_ONE_CM.get().equals(stat.getValue())/* && mc.player.getStats().getValue(stat) > 2040 * 100*/) {
                    final PlayerIO io = new PlayerIO();
                    final Oregon oregon = new Oregon(io, new Random());
                    this.setState(new ActiveState(new Thread(() -> {
                        try {
                            oregon.run();
                        } finally {
                            mc.execute(() -> this.setState(new IdleState()));
                        }
                    }, "Oregon Trail 1978"), io));
                }
            });
        }
        if (screen instanceof ChatScreen && (event.getKeyCode() == GLFW.GLFW_KEY_ENTER || event.getKeyCode() == GLFW.GLFW_KEY_KP_ENTER)) {
            screen.children().stream()
                .filter(EditBox.class::isInstance)
                .map(EditBox.class::cast)
                .findFirst().ifPresent(field -> {
                    if (this.state.onChat(field)) {
                        event.setCanceled(true);
                    }
                });
        }
    }

    private Optional<Stat<?>> getSelectedStat(final StatsScreen screen) {
        final ObjectSelectionList<?> list = screen.getActiveList();
        if (list == null) {
            return Optional.empty();
        }
        final Class<?> classCustomStatsList$Entry;
        try {
            classCustomStatsList$Entry = Class.forName("net.minecraft.client.gui.screens.achievement.StatsScreen$GeneralStatisticsList$Entry");
        } catch (final ClassNotFoundException e) {
            LOGGER.error("Unable to lookup custom stat entry class", e);
            return Optional.empty();
        }
        final ObjectSelectionList.Entry<?> entry = list.getSelected();
        if (!classCustomStatsList$Entry.isInstance(entry)) {
            return Optional.empty();
        }
        final String statFieldName = ObfuscationReflectionHelper.remapName(INameMappingService.Domain.FIELD, "f_97001_");
        final Stat<?> stat;
        try {
            final Field fieldStat = classCustomStatsList$Entry.getDeclaredField(statFieldName);
            fieldStat.setAccessible(true);
            stat = (Stat<?>) fieldStat.get(entry);
        } catch (final NoSuchFieldException | IllegalAccessException e) {
            LOGGER.error("Unable to retrieve stat from entry; fields: {}", Arrays.asList(StatsScreen.class.getDeclaredFields()), e);
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

        abstract boolean onChat(final EditBox field);
    }

    static class IdleState extends State {
        @Override
        void start() {
        }

        @Override
        void stop() {
        }

        @Override
        public boolean onChat(final EditBox field) {
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
            Minecraft.getInstance().setScreen(new ChatScreen("? "));
        }

        @Override
        void stop() {
            this.thread.interrupt();
            try {
                this.thread.join();
            } catch (final InterruptedException ignored) {
            }
            final Minecraft mc = Minecraft.getInstance();
            if (mc.screen instanceof ChatScreen) {
                mc.setScreen(null);
            }
        }

        @Override
        public boolean onChat(final EditBox field) {
            final String text = field.getValue();
            if (text.startsWith("?")) {
                this.io.add(text.substring(1).trim());
                field.setValue("? ");
                return true;
            }
            return false;
        }
    }

    static class PlayerIO implements BasicProgram.IO {
        final BlockingDeque<String> in = new LinkedBlockingDeque<>();

        void add(final String s) {
            this.in.addLast(s);
            final LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                player.sendMessage(new TextComponent(s).withStyle(ChatFormatting.ITALIC, ChatFormatting.WHITE), Util.NIL_UUID);
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
            mc.execute(() -> {
                final LocalPlayer player = mc.player;
                if (player != null) {
                    player.sendMessage(new TextComponent(s).withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY), Util.NIL_UUID);
                }
            });
        }
    }
}
