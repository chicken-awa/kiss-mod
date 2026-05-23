package com.awa.kissmod.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class KissS2CPacket {
    public static final Identifier PACKET_ID = new Identifier("kiss-mod", "kiss_entity_s2c_packet");

    private final UUID pattedEntityUuid;
    private final UUID whoPattedUuid;

    public KissS2CPacket(UUID pattedEntityUuid, UUID whoPattedUuid) {
        this.pattedEntityUuid = pattedEntityUuid;
        this.whoPattedUuid    = whoPattedUuid;
    }

    public KissS2CPacket(PacketByteBuf buf) {
        this.pattedEntityUuid = buf.readUuid();
        this.whoPattedUuid    = buf.readUuid();
    }


    public void write(PacketByteBuf buf) {
        buf.writeUuid(this.pattedEntityUuid);
        buf.writeUuid(this.whoPattedUuid);
    }

    public UUID getPattedEntityUuid() {
        return this.pattedEntityUuid;
    }

    public UUID getWhoPattedUuid() {
        return this.whoPattedUuid;
    }

}
