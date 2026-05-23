package com.awa.kissmod.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class HandshakeS2CPacket {
    public static final Identifier PACKET_ID = new Identifier("kiss-mod", "handshake_s2c");

    public HandshakeS2CPacket() {}

    public HandshakeS2CPacket(PacketByteBuf buf) {}

    public void write(PacketByteBuf buf) {}
}
