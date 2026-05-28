package com.awa.kissmod.client;

import com.awa.kissmod.KissMod;
import com.awa.kissmod.KissModConfig;
import com.awa.kissmod.packet.HandshakeC2SPacket;
import com.awa.kissmod.packet.HandshakeS2CPacket;
import com.awa.kissmod.packet.KissC2SPacket;
import com.awa.kissmod.packet.KissS2CPacket;
import com.awa.kissmod.proxlib.ProxLibPacketIds;
import me.enderkill98.proxlib.ProxPacketIdentifier;
import me.enderkill98.proxlib.client.ProxLib;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import org.slf4j.Logger;

import java.io.*;
import java.util.List;

public class KissModNetworkHandler {

    private static boolean serverHasMod = false;
    private static Thread handshakeThread;
    private static String currentServerAddress;
    private static final Logger LOGGER = KissMod.LOGGER;

    public static void registerHandlers() {
        registerProxLibHandler();
    }

    //加入服务器发握手包
    @SubscribeEvent
    public static void onPlayerLoggedIn(ClientPlayerNetworkEvent.LoggingIn event) {
        serverHasMod = false;
        ServerData serverInfo = Minecraft.getInstance().getCurrentServer();
        if (serverInfo != null) {
            currentServerAddress = serverInfo.ip;
        }
        if (KissModConfig.debugLogging) {
            LOGGER.info("服务器地址: {}", currentServerAddress);
        }
        sendHandshakeWithRetry();
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(ClientPlayerNetworkEvent.LoggingOut event) {
        if (handshakeThread != null && handshakeThread.isAlive()) {
            handshakeThread.interrupt();
            handshakeThread = null;
        }
    }

    private static void sendHandshakeWithRetry() {
        if (KissModConfig.debugLogging) {
            LOGGER.info("发送握手包");
        }
        PacketDistributor.sendToServer(new HandshakeC2SPacket());

        handshakeThread = new Thread(() -> {
            try {
                Thread.sleep(1000);
                if (!serverHasMod) {
                    if (KissModConfig.debugLogging) {
                        LOGGER.info("握手包重发(1/2)");
                    }
                    Minecraft.getInstance().execute(() -> PacketDistributor.sendToServer(new HandshakeC2SPacket()));
                }
            } catch (InterruptedException e) {
                return;
            }

            try {
                Thread.sleep(5000);
                if (!serverHasMod) {
                    if (KissModConfig.debugLogging) {
                        LOGGER.info("握手包重发(2/2)");
                    }
                    Minecraft.getInstance().execute(() -> PacketDistributor.sendToServer(new HandshakeC2SPacket()));
                }
            } catch (InterruptedException ignored) {
            }
        });handshakeThread.start();
    }
    @EventBusSubscriber(modid = KissMod.MOD_ID, value = Dist.CLIENT)
    public static class ModBusEvents {
        @SubscribeEvent
        public static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
            var registrar = event.registrar(KissMod.MOD_ID);

            registrar.playToClient(KissS2CPacket.TYPE, KissS2CPacket.CODEC, (payload, context) -> {
                context.enqueueWork(() -> {
                    if (KissModConfig.debugLogging) {
                        LOGGER.info("接收到了来自服务器的数据包 {}", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
                    }
                    Minecraft client = Minecraft.getInstance();
                    ClientLevel world = client.level;
                    if (world == null || client.player == null) return;
                    if (!KissModConfig.showOthersKiss) return;
                    if (client.player.getUUID().equals(payload.getWhoPattedUuid())) return;
                    UUID targetUuid = payload.getPattedEntityUuid();
                    for (Entity entity : world.entitiesForRendering()) {
                        if (entity.getUUID().equals(targetUuid)) {
                            KissModEffectHandler.triggerEffect(entity, world);
                            break;
                        }
                    }
                });
            });

            registrar.playToClient(HandshakeS2CPacket.TYPE, HandshakeS2CPacket.CODEC, (payload, context) -> {
                serverHasMod = true;
                LOGGER.info("服务器安装了kiss-mod");
            });
        }
    }

    public static void sendKissPacket(Entity target) {
        if (!serverHasMod) {
            if (KissModConfig.proxLibEnabled) {
                sendProxLibPacket(target);
            }
            return;
        }
        UUID senderUuid = null;
        if (Minecraft.getInstance().player != null) {
            senderUuid = Minecraft.getInstance().player.getUUID();
        }
        if (KissModConfig.showOwnKiss) {
            if (KissModConfig.debugLogging) {
                LOGGER.info("客户端发送数据包{}", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
            }
            PacketDistributor.sendToServer(new KissC2SPacket(target.getUUID(), senderUuid));
        }
    }


    private static void sendProxLibPacket(Entity target) {
        if (!KissModConfig.proxLibEnabled) return;
        if (!KissModConfig.showOwnKiss) return;

        if (!checkServerInList(currentServerAddress, KissModConfig.proxLibServerList, KissModConfig.proxLibWhitelistMode)) {
            if (KissModConfig.debugLogging) {
                LOGGER.info("服务器地址不满足黑/白名单");
            }
            return;
        }

        UUID senderUuid = null;
        if (Minecraft.getInstance().player != null) {
            senderUuid = Minecraft.getInstance().player.getUUID();
        }

        try {
            ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
            DataOutputStream dataOut = new DataOutputStream(bytesOut);

            dataOut.writeLong(target.getUUID().getMostSignificantBits());
            dataOut.writeLong(target.getUUID().getLeastSignificantBits());
            if (senderUuid != null) {
                dataOut.writeLong(senderUuid.getMostSignificantBits());
                dataOut.writeLong(senderUuid.getLeastSignificantBits());
            }
            var identifier = ProxPacketIdentifier.of(ProxLibPacketIds.VENDOR_ID, ProxLibPacketIds.PACKET_ID);
            int packets = ProxLib.sendPacket(Minecraft.getInstance(), identifier, bytesOut.toByteArray());
            if (KissModConfig.debugLogging) {
                LOGGER.info("客户端发送ProxLib数据包，使用了 {} 个数据包", packets);
            }
        } catch (IOException e) {
            LOGGER.error("客户端发送ProxLib数据包失败", e);
        }
    }

    private static boolean checkServerInList(String currentServer, List<String> serverList, boolean whitelistMode) {
        if (currentServer == null || serverList == null || serverList.isEmpty()) {
            return !whitelistMode;
        }

        for (String server : serverList) {
            if (!server.contains(":")) {
                server = server + ":25565";
            }
            if (!currentServer.contains(":")) {
                currentServer = currentServer + ":25565";
            }
            if (KissModConfig.debugLogging) {
                LOGGER.info("选到了{}",server);
            }
            if (server.equals(currentServer)) {
                return whitelistMode;
            }
        }

        return !whitelistMode;
    }


    private static void registerProxLibHandler() {
        var identifier = ProxPacketIdentifier.of(ProxLibPacketIds.VENDOR_ID, ProxLibPacketIds.PACKET_ID);
        ProxLib.addHandlerFor(identifier, (sender, id, data) -> {
            if (KissModConfig.debugLogging) {
                LOGGER.info("接收到了ProxLib数据包 from {}", sender.getUUID());
            }
            if (!KissModConfig.showOthersKiss) return;

            try {
                DataInputStream dataIn = new DataInputStream(new ByteArrayInputStream(data));

                long targetMostSigBits = dataIn.readLong();
                long targetLeastSigBits = dataIn.readLong();
                UUID targetUuid = new UUID(targetMostSigBits, targetLeastSigBits);

                long senderMostSigBits = dataIn.readLong();
                long senderLeastSigBits = dataIn.readLong();
                UUID senderUuid = new UUID(senderMostSigBits, senderLeastSigBits);

                Minecraft client = Minecraft.getInstance();
                if (client.player != null && client.player.getUUID().equals(senderUuid)) return;

                ClientLevel world = client.level;
                if (world == null) return;

                for (Entity entity : world.entitiesForRendering()) {
                    if (entity.getUUID().equals(targetUuid)) {
                        KissModEffectHandler.triggerEffect(entity, world);
                        break;
                    }
                }
            } catch (IOException e) {
                LOGGER.error("处理ProxLib数据包失败", e);
            }
        });
    }
}
