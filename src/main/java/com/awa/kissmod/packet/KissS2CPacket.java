package com.awa.kissmod.packet;

import java.util.UUID;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}

public class KissS2CPacket implements CustomPacketPayload {
    //? if >=1.21.11 {
    /*public static final Identifier PACKET_ID = Identifier.fromNamespaceAndPath("kiss-mod", "kiss_entity_s2c_packet");
    *///?} else {
    public static final ResourceLocation PACKET_ID = ResourceLocation.fromNamespaceAndPath("kiss-mod", "kiss_entity_s2c_packet");
    //?}
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
