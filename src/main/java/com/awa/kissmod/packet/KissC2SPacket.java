package com.awa.kissmod.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class KissC2SPacket {
    public static final Identifier PACKET_ID = new Identifier("kiss-mod", "kiss_entity_c2s_packet");

    private final UUID kissedEntityUuid;
    private final UUID senderUuid;

    public KissC2SPacket(UUID kissedEntityUuid, UUID senderUuid) {
        this.kissedEntityUuid = kissedEntityUuid;
        this.senderUuid = senderUuid;
    }

    public KissC2SPacket(PacketByteBuf buf) {
        this.kissedEntityUuid = buf.readUuid();
        this.senderUuid = buf.readUuid();
    }


    public UUID getKissedEntityUuid() {
        return this.kissedEntityUuid;
    }

    public UUID getSenderUuid() {
        return this.senderUuid;
    }

    public void write(PacketByteBuf buf) {
        buf.writeUuid(this.kissedEntityUuid);
        buf.writeUuid(this.senderUuid);
    }
}

