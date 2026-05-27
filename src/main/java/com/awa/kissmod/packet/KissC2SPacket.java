package com.awa.kissmod.packet;

import java.util.UUID;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public class KissC2SPacket implements CustomPacketPayload {
    public static final ResourceLocation PACKET_ID = ResourceLocation.fromNamespaceAndPath("kiss-mod", "kiss_entity_c2s_packet");
    public static final Type<KissC2SPacket> TYPE = new Type<>(PACKET_ID);
    public static final net.minecraft.network.codec.StreamCodec<RegistryFriendlyByteBuf, KissC2SPacket> CODEC = net.minecraft.network.protocol.common.custom.CustomPacketPayload.codec(KissC2SPacket::write, KissC2SPacket::new);

    private final UUID kissedEntityUuid;
    private final UUID senderUuid;

    public KissC2SPacket(UUID kissedEntityUuid, UUID senderUuid) {
        this.kissedEntityUuid = kissedEntityUuid;
        this.senderUuid = senderUuid;
    }

    public KissC2SPacket(RegistryFriendlyByteBuf buf) {
        this.kissedEntityUuid = buf.readUUID();
        this.senderUuid = buf.readUUID();
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public UUID getKissedEntityUuid() {
        return this.kissedEntityUuid;
    }

    public UUID getSenderUuid() {
        return this.senderUuid;
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeUUID(this.kissedEntityUuid);
        buf.writeUUID(this.senderUuid);
    }
}

