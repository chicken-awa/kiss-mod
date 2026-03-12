package com.awa.kissmod.packet;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class HandshakeS2CPacket implements CustomPayload {
    public static final Identifier PACKET_ID = Identifier.of("kiss-mod", "handshake_s2c");
    public static final Id<HandshakeS2CPacket> TYPE = new Id<>(PACKET_ID);
    public static final net.minecraft.network.codec.PacketCodec<RegistryByteBuf, HandshakeS2CPacket> CODEC =
            net.minecraft.network.packet.CustomPayload.codecOf(HandshakeS2CPacket::write, HandshakeS2CPacket::new);

    public HandshakeS2CPacket() {}

    public HandshakeS2CPacket(RegistryByteBuf buf) {}

    @Override
    public Id<? extends CustomPayload> getId() {
        return TYPE;
    }

    public void write(RegistryByteBuf buf) {}
}
