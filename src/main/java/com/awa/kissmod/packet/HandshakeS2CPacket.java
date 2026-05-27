package com.awa.kissmod.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public class HandshakeS2CPacket implements CustomPacketPayload {
    public static final ResourceLocation PACKET_ID = ResourceLocation.fromNamespaceAndPath("kiss-mod", "handshake_s2c");
    public static final Type<HandshakeS2CPacket> TYPE = new Type<>(PACKET_ID);
    public static final net.minecraft.network.codec.StreamCodec<RegistryFriendlyByteBuf, HandshakeS2CPacket> CODEC =
            net.minecraft.network.protocol.common.custom.CustomPacketPayload.codec(HandshakeS2CPacket::write, HandshakeS2CPacket::new);

    public HandshakeS2CPacket() {}

    public HandshakeS2CPacket(RegistryFriendlyByteBuf buf) {}

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void write(RegistryFriendlyByteBuf buf) {}
}
