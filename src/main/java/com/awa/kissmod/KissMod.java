package com.awa.kissmod;

import com.awa.kissmod.packet.HandshakeC2SPacket;
import com.awa.kissmod.packet.HandshakeS2CPacket;
import com.awa.kissmod.packet.KissC2SPacket;
import com.awa.kissmod.packet.KissS2CPacket;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class KissMod implements ModInitializer {
	/*
	啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊饱饱你是一个一个一个一个哈基米啊啊啊啊啊啊！！！
	awa! 		ヾ(≧▽≦*)o   			 ૮(˶ᵔ ᵕ ᵔ˶)ა   		   	qwq!
	awa? 		(・ω< )★    			 ヾ(´･ω･｀)ﾉ 		    qwq?
	awa~ 		o((>ω< ))o  			 ο(=•ω＜=)ρ⌒☆			qwq~
	🐱喵喵喵喵喵喵喵喵喵喵喵喵喵喵喵喵喵喵~~~~~~~~~~~~~~~~~~~~~~~~~~~🐾🐾🐾
 	*/
	public static final String MOD_ID = "kiss-mod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final Identifier CUSTOM_SOUND_ID = Identifier.fromNamespaceAndPath(MOD_ID, "custom_sound");
	public static final SoundEvent CUSTOM_SOUND_EVENT = Registry.register(
			BuiltInRegistries.SOUND_EVENT,
			CUSTOM_SOUND_ID,
			SoundEvent.createVariableRangeEvent(CUSTOM_SOUND_ID)
	);
	public static final Identifier CUSTOM_SOUND1_ID = Identifier.fromNamespaceAndPath(MOD_ID, "custom_sound1");
	public static final SoundEvent CUSTOM_SOUND1_EVENT = Registry.register(
			BuiltInRegistries.SOUND_EVENT,
			CUSTOM_SOUND1_ID,
			SoundEvent.createVariableRangeEvent(CUSTOM_SOUND1_ID)
	);

	public static final Identifier CUSTOM_SOUND2_ID = Identifier.fromNamespaceAndPath(MOD_ID, "custom_sound2");
	public static final SoundEvent CUSTOM_SOUND2_EVENT = Registry.register(
			BuiltInRegistries.SOUND_EVENT,
			CUSTOM_SOUND2_ID,
			SoundEvent.createVariableRangeEvent(CUSTOM_SOUND2_ID)
	);
	static {
			PayloadTypeRegistry.playS2C().register(KissS2CPacket.TYPE, KissS2CPacket.CODEC);
			PayloadTypeRegistry.playC2S().register(KissC2SPacket.TYPE, KissC2SPacket.CODEC);
			PayloadTypeRegistry.playS2C().register(HandshakeS2CPacket.TYPE, HandshakeS2CPacket.CODEC);
			PayloadTypeRegistry.playC2S().register(HandshakeC2SPacket.TYPE, HandshakeC2SPacket.CODEC);
		}
	@Override
	public void onInitialize() {
		System.out.println("KissMod initialized!");
		registerNetworkReceiver();
	}
	private void registerNetworkReceiver() {
		ServerPlayNetworking.registerGlobalReceiver(KissC2SPacket.TYPE, (payload, context) -> {
			ServerPlayer player = context.player();
			UUID targetUuid = payload.getKissedEntityUuid();
			UUID senderUuid = payload.getSenderUuid();
			World world = player.getEntityWorld();
			Entity target = ((ServerLevel) world).getEntity(targetUuid);
			if (target != null) {
				// 向所有附近玩家发送数据包 排除发送者自己
				KissS2CPacket broadcastPayload = new KissS2CPacket(target.getUUID(), senderUuid);
				for (ServerPlayer nearbyPlayer : ((ServerLevel) world).players()) {
					if (target.distanceToSqr(nearbyPlayer) > 192 * 192) continue;
					if (nearbyPlayer.getUUID().equals(senderUuid)) continue;
					ServerPlayNetworking.send(nearbyPlayer, broadcastPayload);
				}
			}
		});

		ServerPlayNetworking.registerGlobalReceiver(HandshakeC2SPacket.TYPE, (payload, context) -> ServerPlayNetworking.send(context.player(), new HandshakeS2CPacket()));
	}
}