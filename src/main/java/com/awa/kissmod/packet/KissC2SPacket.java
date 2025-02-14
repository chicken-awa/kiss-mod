package com.awa.kissmod.packet;

import com.awa.kissmod.KissMod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class KissC2SPacket implements CustomPayload {
    public static final Identifier PACKET_ID = typeId("kiss_entity_c2s_packet");
    public static final Id<KissC2SPacket> TYPE = new Id<>(PACKET_ID);
    public static final net.minecraft.network.codec.PacketCodec<RegistryByteBuf, KissC2SPacket> CODEC = net.minecraft.network.packet.CustomPayload.codecOf(KissC2SPacket::write, KissC2SPacket::new);

    private final UUID kissedEntityUuid;
    private final UUID senderUuid; // 新增发送者UUID字段

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

    public KissC2SPacket(UUID kissedEntityUuid, UUID senderUuid) {
        this.kissedEntityUuid = kissedEntityUuid;
        this.senderUuid = senderUuid;
    }

    public KissC2SPacket(RegistryByteBuf buf) {
        this.kissedEntityUuid = buf.readUuid();
        this.senderUuid = buf.readUuid(); // 从缓冲区读取新增字段
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return TYPE;
    }

    public UUID getKissedEntityUuid() {
        return this.kissedEntityUuid;
    }

    public UUID getSenderUuid() {
        return this.senderUuid;
    }

    public void write(RegistryByteBuf buf) {
        buf.writeUuid(this.kissedEntityUuid);
        buf.writeUuid(this.senderUuid); // 写入新增字段
    }
}

