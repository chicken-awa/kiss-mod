package com.awa.kissmod;

import com.awa.kissmod.packet.HandshakeC2SPacket;
import com.awa.kissmod.packet.HandshakeS2CPacket;
import com.awa.kissmod.packet.KissC2SPacket;
import com.awa.kissmod.packet.KissS2CPacket;
import net.minecraft.core.registries.BuiltInRegistries;
//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.function.Supplier;

@Mod(KissMod.MOD_ID)
public class KissMod {
	/*
	啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊饱饱你是一个一个一个一个哈基米啊啊啊啊啊啊！！！
	awa! 		ヾ(≧▽≦*)o   			 ૮(˶ᵔ ᵕ ᵔ˶)ა   		   	qwq!
	awa? 		(・ω< )★    			 ヾ(´･ω･｀)ﾉ 		    qwq?
	awa~ 		o((>ω< ))o  			 ο(=•ω＜=)ρ⌒☆			qwq~
	🐱喵喵喵喵喵喵喵喵喵喵喵喵喵喵喵喵喵喵~~~~~~~~~~~~~~~~~~~~~~~~~~~🐾🐾🐾
 	*/
	public static final String MOD_ID = "kiss_mod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
			DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, "kiss-mod");

	public static final Supplier<SoundEvent> CUSTOM_SOUND_EVENT = SOUND_EVENTS.register(
			"custom_sound", () -> SoundEvent.createVariableRangeEvent(
					//? if >=1.21.11 {
					/*Identifier.fromNamespaceAndPath("kiss-mod", "custom_sound")));
					*///?} else {
					ResourceLocation.fromNamespaceAndPath("kiss-mod", "custom_sound")));
					//?}
	public static final Supplier<SoundEvent> CUSTOM_SOUND1_EVENT = SOUND_EVENTS.register(
			"custom_sound1", () -> SoundEvent.createVariableRangeEvent(
					//? if >=1.21.11 {
					/*Identifier.fromNamespaceAndPath("kiss-mod", "custom_sound1")));
					*///?} else {
					ResourceLocation.fromNamespaceAndPath("kiss-mod", "custom_sound1")));
					//?}
	public static final Supplier<SoundEvent> CUSTOM_SOUND2_EVENT = SOUND_EVENTS.register(
			"custom_sound2", () -> SoundEvent.createVariableRangeEvent(
					//? if >=1.21.11 {
					/*Identifier.fromNamespaceAndPath("kiss-mod", "custom_sound2")));
					*///?} else {
					ResourceLocation.fromNamespaceAndPath("kiss-mod", "custom_sound2")));
					//?}
	public KissMod(IEventBus modEventBus) {
		System.out.println("KissMod initialized!");
		SOUND_EVENTS.register(modEventBus);
		modEventBus.addListener(this::registerPayloads);
	}

	private void registerPayloads(RegisterPayloadHandlersEvent event) {
		var registrar = event.registrar(MOD_ID).optional();

		registrar.playToServer(KissC2SPacket.TYPE, KissC2SPacket.CODEC, (payload, context) -> context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            UUID targetUuid = payload.getKissedEntityUuid();
            UUID senderUuid = payload.getSenderUuid();
            Level world = player.level();
            Entity target = ((ServerLevel) world).getEntity(targetUuid);
            if (target != null) {
                // 向所有附近玩家发送数据包 排除发送者自己
                KissS2CPacket broadcastPayload = new KissS2CPacket(target.getUUID(), senderUuid);
                for (ServerPlayer nearbyPlayer : ((ServerLevel) world).players()) {
                    if (target.distanceToSqr(nearbyPlayer) > 192 * 192) continue;
                    if (nearbyPlayer.getUUID().equals(senderUuid)) continue;
                    PacketDistributor.sendToPlayer(nearbyPlayer, broadcastPayload);
                }
            }
        }));

		registrar.playToServer(HandshakeC2SPacket.TYPE, HandshakeC2SPacket.CODEC, (payload, context) -> context.reply(new HandshakeS2CPacket()));
	}
}