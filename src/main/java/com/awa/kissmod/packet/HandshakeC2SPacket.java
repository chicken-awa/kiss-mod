package com.awa.kissmod.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class HandshakeC2SPacket implements CustomPacketPayload {
    public static final Identifier PACKET_ID = Identifier.fromNamespaceAndPath("kiss-mod", "handshake_c2s");
    public static final Type<HandshakeC2SPacket> TYPE = new Type<>(PACKET_ID);
    public static final net.minecraft.network.codec.StreamCodec<RegistryFriendlyByteBuf, HandshakeC2SPacket> CODEC =
            net.minecraft.network.protocol.common.custom.CustomPacketPayload.codec(HandshakeC2SPacket::write, HandshakeC2SPacket::new);

    public HandshakeC2SPacket() {}

    public HandshakeC2SPacket(RegistryFriendlyByteBuf buf) {}

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void write(RegistryFriendlyByteBuf buf) {}
}
