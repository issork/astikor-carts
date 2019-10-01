package de.mennomax.astikorcarts.network;

import de.mennomax.astikorcarts.AstikorCarts;
import de.mennomax.astikorcarts.network.packets.CPacketActionKey;
import de.mennomax.astikorcarts.network.packets.CPacketToggleSlow;
import de.mennomax.astikorcarts.network.packets.SPacketDrawnUpdate;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry.ChannelBuilder;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {

    private static int id = 0;
    public static final String VERSION = "1";
    public static final SimpleChannel channel = ChannelBuilder.named(new ResourceLocation(AstikorCarts.MODID, "main"))
            .networkProtocolVersion(() -> VERSION)
            .clientAcceptedVersions(VERSION::equals)
            .serverAcceptedVersions(VERSION::equals)
            .simpleChannel();

    public static void registerPackets() {
        channel.registerMessage(id++, CPacketActionKey.class, CPacketActionKey::encode, CPacketActionKey::decode, CPacketActionKey::handle);
        channel.registerMessage(id++, CPacketToggleSlow.class, CPacketToggleSlow::encode, CPacketToggleSlow::decode, CPacketToggleSlow::handle);
        channel.registerMessage(id++, SPacketDrawnUpdate.class, SPacketDrawnUpdate::encode, SPacketDrawnUpdate::decode, SPacketDrawnUpdate::handle);
    }

}
