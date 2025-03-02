package org.kissmod.kissMod.packet;

import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import org.kissmod.kissMod.packet.handler.PacketHandler;

import java.util.HashMap;
import java.util.Map;

public class KissPacketListener implements PluginMessageListener {

    private final Map<String, PacketHandler> handlers = new HashMap<>();

    @Override
    public void onPluginMessageReceived(@NotNull String s, @NotNull Player player, byte[] bytes) {
        PacketHandler packetHandler = this.handlers.get(s);
        if (packetHandler == null) {
            return;
        }
        packetHandler.handle(player, ByteStreams.newDataInput(bytes));
    }

    public void registerPacket(PacketHandler handler) {
        this.handlers.put(handler.getPacketID(), handler);
    }
}