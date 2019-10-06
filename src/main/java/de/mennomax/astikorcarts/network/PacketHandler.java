package de.mennomax.astikorcarts.network;

import de.mennomax.astikorcarts.AstikorCarts;
import de.mennomax.astikorcarts.network.packets.CPacketActionKey;
import de.mennomax.astikorcarts.network.packets.CPacketOpenCargoCartGui;
import de.mennomax.astikorcarts.network.packets.CPacketToggleSlow;
import de.mennomax.astikorcarts.network.packets.SPacketDrawnUpdate;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.network.NetworkRegistry.ChannelBuilder;
import net.minecraftforge.fml.network.simple.SimpleChannel;

@EventBusSubscriber(modid = AstikorCarts.MODID, bus = Bus.MOD)
public class PacketHandler {

    private static int id = 0;
    public static final String VERSION = "1";
    public static final SimpleChannel CHANNEL = ChannelBuilder.named(new ResourceLocation(AstikorCarts.MODID, "main"))
            .networkProtocolVersion(() -> VERSION)
            .clientAcceptedVersions(VERSION::equals)
            .serverAcceptedVersions(VERSION::equals)
            .simpleChannel();

    @SubscribeEvent
    public static void registerPackets(final FMLCommonSetupEvent event) {        
        CHANNEL.registerMessage(id++, CPacketActionKey.class, CPacketActionKey::encode, CPacketActionKey::decode, CPacketActionKey::handle);
        CHANNEL.registerMessage(id++, CPacketToggleSlow.class, CPacketToggleSlow::encode, CPacketToggleSlow::decode, CPacketToggleSlow::handle);
        CHANNEL.registerMessage(id++, SPacketDrawnUpdate.class, SPacketDrawnUpdate::encode, SPacketDrawnUpdate::decode, SPacketDrawnUpdate::handle);
        CHANNEL.registerMessage(id++, CPacketOpenCargoCartGui.class, CPacketOpenCargoCartGui::encode, CPacketOpenCargoCartGui::decode, CPacketOpenCargoCartGui::handle);
    }

}
