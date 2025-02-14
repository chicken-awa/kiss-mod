package com.awa.kissmod.packet;

import com.awa.kissmod.KissMod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class KissS2CPacket implements CustomPayload {

    public static final Identifier PACKET_ID = typeId("kiss_entity_s2c_packet");


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

    public static Identifier typeId(String id) {
        String namespace = KissMod.MOD_ID;
        String path = id;
        String[] split = path.split(":");
        if (split.length >= 2) {
            namespace = split[0];
            path      = split[1];
        }

        return Identifier.of(namespace, path);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return TYPE;
    }

}
