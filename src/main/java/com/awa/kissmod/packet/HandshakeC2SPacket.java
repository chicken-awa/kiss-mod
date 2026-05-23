package com.awa.kissmod.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class HandshakeC2SPacket {
    public static final Identifier PACKET_ID = new Identifier("kiss-mod", "handshake_c2s");

    public HandshakeC2SPacket() {}

    public HandshakeC2SPacket(PacketByteBuf buf) {}

    public void write(PacketByteBuf buf) {}
}
