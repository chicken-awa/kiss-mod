package org.kissmod.kissMod.packet;

import org.bukkit.plugin.messaging.Messenger;
import org.kissmod.kissMod.KissModPlugin;
import org.kissmod.kissMod.packet.handler.KissPacketHandler;

public class KissPacketManager {
    public static final String KISS_C2S_PACKET_ID = KissModPlugin.id("kiss_entity_c2s_packet");
    public static final String KISS_S2C_PACKET_ID = KissModPlugin.id("kiss_entity_s2c_packet");

    public static void register() {
        KissPacketListener listener = new KissPacketListener();
        listener.registerPacket(new KissPacketHandler());
        KissModPlugin plugin = KissModPlugin.getInstance();
        Messenger messenger = plugin.getServer().getMessenger();

        messenger.registerIncomingPluginChannel(plugin, KISS_C2S_PACKET_ID, listener);
        messenger.registerOutgoingPluginChannel(plugin, KISS_S2C_PACKET_ID);
    }
}
