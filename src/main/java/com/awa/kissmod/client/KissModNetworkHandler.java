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
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;

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
        registerConnectionEvents();
        registerClientNetworkReceiver();
        registerProxLibHandler();
    }

    //加入服务器发握手包
    private static void registerConnectionEvents() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            serverHasMod = false;
            ServerInfo serverInfo = MinecraftClient.getInstance().getCurrentServerEntry();
            if (serverInfo != null) {
                currentServerAddress = serverInfo.address;
            }

            if (KissModConfig.debugLogging) {
                LOGGER.info("服务器地址: {}", currentServerAddress);
            }
            sendHandshakeWithRetry();
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            if (handshakeThread != null && handshakeThread.isAlive()) {
                handshakeThread.interrupt();
                handshakeThread = null;
            }
        });
    }

    private static void sendHandshakeWithRetry() {
        if (KissModConfig.debugLogging) {
            LOGGER.info("发送握手包");
        }
        ClientPlayNetworking.send(new HandshakeC2SPacket());

        handshakeThread = new Thread(() -> {
            try {
                Thread.sleep(1000);
                if (!serverHasMod) {
                    if (KissModConfig.debugLogging) {
                        LOGGER.info("握手包重发(1/2)");
                    }
                    MinecraftClient.getInstance().execute(() -> ClientPlayNetworking.send(new HandshakeC2SPacket()));
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
                    MinecraftClient.getInstance().execute(() -> ClientPlayNetworking.send(new HandshakeC2SPacket()));
                }
            } catch (InterruptedException ignored) {
            }
        });handshakeThread.start();
    }

    private static void registerClientNetworkReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(KissS2CPacket.TYPE, (payload, context) -> {
            if (KissModConfig.debugLogging) {
                LOGGER.info("接收到了来自服务器的数据包 {}", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
            }
            MinecraftClient client = MinecraftClient.getInstance();
            ClientWorld world = client.world;

            if (world == null || client.player == null) return;
            if (!KissModConfig.showOthersKiss) return;
            if (client.player.getUuid().equals(payload.getWhoPattedUuid())) return;
            UUID targetUuid = payload.getPattedEntityUuid();
            for (Entity entity : world.getEntities()) {
                if (entity.getUuid().equals(targetUuid)) {
                    KissModEffectHandler.triggerEffect(entity, world);
                    break;
                }
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(HandshakeS2CPacket.TYPE, (payload, context) -> {
            serverHasMod = true;
            LOGGER.info("服务器安装了kiss-mod");
        });
    }

    public static void sendKissPacket(Entity target) {
        if (!serverHasMod) {
            if (KissModConfig.proxLibEnabled) {
                sendProxLibPacket(target);
            }
            return;
        }
        UUID senderUuid = null;
        if (MinecraftClient.getInstance().player != null) {
            senderUuid = MinecraftClient.getInstance().player.getUuid();
        }
        if (KissModConfig.showOwnKiss) {
            if (KissModConfig.debugLogging) {
                LOGGER.info("客户端发送数据包{}", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
            }
            ClientPlayNetworking.send(new KissC2SPacket(target.getUuid(), senderUuid));
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
        if (MinecraftClient.getInstance().player != null) {
            senderUuid = MinecraftClient.getInstance().player.getUuid();
        }

        try {
            ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
            DataOutputStream dataOut = new DataOutputStream(bytesOut);

            dataOut.writeLong(target.getUuid().getMostSignificantBits());
            dataOut.writeLong(target.getUuid().getLeastSignificantBits());
            if (senderUuid != null) {
                dataOut.writeLong(senderUuid.getMostSignificantBits());
                dataOut.writeLong(senderUuid.getLeastSignificantBits());
            }
            var identifier = ProxPacketIdentifier.of(ProxLibPacketIds.VENDOR_ID, ProxLibPacketIds.PACKET_ID);
            int packets = ProxLib.sendPacket(MinecraftClient.getInstance(), identifier, bytesOut.toByteArray());
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
                LOGGER.info("接收到了ProxLib数据包 from {}", sender.getUuid());
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

                MinecraftClient client = MinecraftClient.getInstance();
                if (client.player != null && client.player.getUuid().equals(senderUuid)) return;

                ClientWorld world = client.world;
                if (world == null) return;

                for (Entity entity : world.getEntities()) {
                    if (entity.getUuid().equals(targetUuid)) {
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
