package org.kissmod.kissMod.packet.handler;

import com.google.common.io.ByteArrayDataInput;
import org.bukkit.entity.Player;

public interface PacketHandler {

    void handle(Player sender, ByteArrayDataInput buf);

    String getPacketID();
}
