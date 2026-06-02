package com.awa.kissmod.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}

public class HandshakeC2SPacket implements CustomPacketPayload {
    //? if >=1.21.11 {
    /*public static final Identifier PACKET_ID = Identifier.fromNamespaceAndPath("kiss-mod", "handshake_c2s");
    *///?} else {
    public static final ResourceLocation PACKET_ID = ResourceLocation.fromNamespaceAndPath("kiss-mod", "handshake_c2s");
    //?}
    public static final Type<HandshakeC2SPacket> TYPE = new Type<>(PACKET_ID);
    public static final net.minecraft.network.codec.StreamCodec<RegistryFriendlyByteBuf, HandshakeC2SPacket> CODEC =
            net.minecraft.network.protocol.common.custom.CustomPacketPayload.codec(HandshakeC2SPacket::write, HandshakeC2SPacket::new);

    public HandshakeC2SPacket() {}

    public HandshakeC2SPacket(RegistryFriendlyByteBuf buf) {}

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void write(RegistryFriendlyByteBuf buf) {}
}
