package com.awa.kissmod.packet;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class KissS2CPacket implements CustomPayload {

    public static final Identifier PACKET_ID = Identifier.of("kiss-mod", "kiss_entity_s2c_packet");


    public static final Id<KissS2CPacket> TYPE = new Id<>(PACKET_ID);
    public static final net.minecraft.network.codec.PacketCodec<RegistryByteBuf, KissS2CPacket> CODEC = net.minecraft.network.packet.CustomPayload.codecOf(KissS2CPacket::write, KissS2CPacket::new);

    private final UUID pattedEntityUuid;
    private final UUID whoPattedUuid;

    public KissS2CPacket(UUID pattedEntityUuid, UUID whoPattedUuid) {
        this.pattedEntityUuid = pattedEntityUuid;
        this.whoPattedUuid    = whoPattedUuid;
    }

    public KissS2CPacket(RegistryByteBuf buf) {
        this.pattedEntityUuid = buf.readUuid();
        this.whoPattedUuid    = buf.readUuid();
    }


    public void write(RegistryByteBuf buf) {
        buf.writeUuid(this.pattedEntityUuid);
        buf.writeUuid(this.whoPattedUuid);
    }

    public UUID getPattedEntityUuid() {
        return this.pattedEntityUuid;
    }

    public UUID getWhoPattedUuid() {
        return this.whoPattedUuid;
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return TYPE;
    }

}
