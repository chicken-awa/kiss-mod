package com.awa.kissmod;

import com.awa.kissmod.packet.HandshakeC2SPacket;
import com.awa.kissmod.packet.HandshakeS2CPacket;
import com.awa.kissmod.packet.KissC2SPacket;
import com.awa.kissmod.packet.KissS2CPacket;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

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
	public static final ResourceLocation CUSTOM_SOUND_ID = ResourceLocation.fromNamespaceAndPath("kiss-mod", "custom_sound");
	public static final SoundEvent CUSTOM_SOUND_EVENT = Registry.register(
			BuiltInRegistries.SOUND_EVENT,
			CUSTOM_SOUND_ID,
			SoundEvent.createVariableRangeEvent(CUSTOM_SOUND_ID)
	);
	public static final ResourceLocation CUSTOM_SOUND1_ID = ResourceLocation.fromNamespaceAndPath("kiss-mod", "custom_sound1");
	public static final SoundEvent CUSTOM_SOUND1_EVENT = Registry.register(
			BuiltInRegistries.SOUND_EVENT,
			CUSTOM_SOUND1_ID,
			SoundEvent.createVariableRangeEvent(CUSTOM_SOUND1_ID)
	);

	public static final ResourceLocation CUSTOM_SOUND2_ID = ResourceLocation.fromNamespaceAndPath("kiss-mod", "custom_sound2");
	public static final SoundEvent CUSTOM_SOUND2_EVENT = Registry.register(
			BuiltInRegistries.SOUND_EVENT,
			CUSTOM_SOUND2_ID,
			SoundEvent.createVariableRangeEvent(CUSTOM_SOUND2_ID)
	);
	public KissMod(IEventBus modEventBus) {
		System.out.println("KissMod initialized!");
		modEventBus.addListener(this::registerPayloads);
	}

	private void registerPayloads(RegisterPayloadHandlersEvent event) {
		var registrar = event.registrar(MOD_ID);

		registrar.playToServer(KissC2SPacket.TYPE, KissC2SPacket.CODEC, (payload, context) -> {
			context.enqueueWork(() -> {
				ServerPlayer player = (ServerPlayer) context.player();
				UUID targetUuid = payload.getKissedEntityUuid();
				UUID senderUuid = payload.getSenderUuid();
				//? if >=1.21.9{
				/*Level world = player.level();
				 *///?} else{
				Level world = player.level();
				//?}
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
			});
		});

		registrar.playToServer(HandshakeC2SPacket.TYPE, HandshakeC2SPacket.CODEC, (payload, context) -> context.reply(new HandshakeS2CPacket()));
	}
}