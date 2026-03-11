package com.awa.kissmod;

import com.awa.kissmod.packet.KissC2SPacket;
import com.awa.kissmod.packet.KissS2CPacket;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraft.server.world.ServerWorld;

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
	public static final Identifier CUSTOM_SOUND_ID = Identifier.of(MOD_ID, "custom_sound");
	public static final SoundEvent CUSTOM_SOUND_EVENT = Registry.register(
			Registries.SOUND_EVENT,
			CUSTOM_SOUND_ID,
			SoundEvent.of(CUSTOM_SOUND_ID)
	);
	public static final Identifier CUSTOM_SOUND1_ID = Identifier.of(MOD_ID, "custom_sound1");
	public static final SoundEvent CUSTOM_SOUND1_EVENT = Registry.register(
			Registries.SOUND_EVENT,
			CUSTOM_SOUND1_ID,
			SoundEvent.of(CUSTOM_SOUND1_ID)
	);

	public static final Identifier CUSTOM_SOUND2_ID = Identifier.of(MOD_ID, "custom_sound2");
	public static final SoundEvent CUSTOM_SOUND2_EVENT = Registry.register(
			Registries.SOUND_EVENT,
			CUSTOM_SOUND2_ID,
			SoundEvent.of(CUSTOM_SOUND2_ID)
	);
			static {
			PayloadTypeRegistry.playS2C().register(KissS2CPacket.TYPE, KissS2CPacket.CODEC);
			PayloadTypeRegistry.playC2S().register(KissC2SPacket.TYPE, KissC2SPacket.CODEC);
		}
	@Override
	public void onInitialize() {
		System.out.println("KissMod initialized!");
		registerNetworkReceiver();
	}
	private void registerNetworkReceiver() {
		ServerPlayNetworking.registerGlobalReceiver(KissC2SPacket.TYPE, (payload, context) -> {
			ServerPlayerEntity player = context.player();
			UUID targetUuid = payload.getKissedEntityUuid();
			UUID senderUuid = payload.getSenderUuid();
			//? if >=1.21.9{
			/*World world = player.getEntityWorld();
			*///?} else{
			World world = player.getWorld();
			//?}
			Entity target = ((ServerWorld) world).getEntity(targetUuid);
			if (target != null) {
				// 向所有附近玩家发送数据包 排除发送者自己
				KissS2CPacket broadcastPayload = new KissS2CPacket(target.getUuid(), senderUuid);
				for (ServerPlayerEntity nearbyPlayer : ((ServerWorld) world).getPlayers()) {
					if (target.squaredDistanceTo(nearbyPlayer) > 192 * 192) continue;
					if (nearbyPlayer.getUuid().equals(senderUuid)) continue;
					ServerPlayNetworking.send(nearbyPlayer, broadcastPayload);
				}
			}
		});
	}
}