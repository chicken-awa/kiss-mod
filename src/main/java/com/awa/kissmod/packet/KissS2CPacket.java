package com.awa.kissmod.packet;

import java.util.UUID;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public class KissS2CPacket implements CustomPacketPayload {

    public static final Identifier PACKET_ID = Identifier.fromNamespaceAndPath("kiss-mod", "kiss_entity_s2c_packet");


    public static final Type<KissS2CPacket> TYPE = new Type<>(PACKET_ID);
    public static final net.minecraft.network.codec.StreamCodec<RegistryFriendlyByteBuf, KissS2CPacket> CODEC = net.minecraft.network.protocol.common.custom.CustomPacketPayload.codec(KissS2CPacket::write, KissS2CPacket::new);

    private final UUID pattedEntityUuid;
    private final UUID whoPattedUuid;

    public KissS2CPacket(UUID pattedEntityUuid, UUID whoPattedUuid) {
        this.pattedEntityUuid = pattedEntityUuid;
        this.whoPattedUuid    = whoPattedUuid;
    }

    public KissS2CPacket(RegistryFriendlyByteBuf buf) {
        this.pattedEntityUuid = buf.readUUID();
        this.whoPattedUuid    = buf.readUUID();
    }


    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeUUID(this.pattedEntityUuid);
        buf.writeUUID(this.whoPattedUuid);
    }

    public UUID getPattedEntityUuid() {
        return this.pattedEntityUuid;
    }

    public UUID getWhoPattedUuid() {
        return this.whoPattedUuid;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
