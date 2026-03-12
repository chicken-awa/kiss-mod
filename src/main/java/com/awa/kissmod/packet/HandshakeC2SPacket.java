package com.awa.kissmod.packet;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class HandshakeC2SPacket implements CustomPayload {
    public static final Identifier PACKET_ID = Identifier.of("kiss-mod", "handshake_c2s");
    public static final Id<HandshakeC2SPacket> TYPE = new Id<>(PACKET_ID);
    public static final net.minecraft.network.codec.PacketCodec<RegistryByteBuf, HandshakeC2SPacket> CODEC =
            net.minecraft.network.packet.CustomPayload.codecOf(HandshakeC2SPacket::write, HandshakeC2SPacket::new);

    public HandshakeC2SPacket() {}

    public HandshakeC2SPacket(RegistryByteBuf buf) {}

    @Override
    public Id<? extends CustomPayload> getId() {
        return TYPE;
    }

    public void write(RegistryByteBuf buf) {}
}
