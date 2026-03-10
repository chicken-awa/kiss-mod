package com.awa.kissmod.client;

import com.awa.kissmod.KissMod;
import com.awa.kissmod.KissModConfig;
import com.awa.kissmod.packet.KissC2SPacket;
import com.awa.kissmod.packet.KissS2CPacket;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import com.mojang.brigadier.arguments.BoolArgumentType;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

public class KissModClient implements ClientModInitializer {

    private static KeyBinding kissKey;
    private static boolean wasKeyPressed = false;
    private static boolean wasRightClickPressed = false;
    private static long lastTriggerTime = 0;
    private static long lastRightClickTriggerTime = 0;
    private static final long TRIGGER_INTERVAL = 175;
    private static final Logger LOGGER = KissMod.LOGGER;

    @Override
    public void onInitializeClient() {
        if (FabricLoader.getInstance().isModLoaded("cloth-config")) {
            KissMod.LOGGER.info("Cloth Config detected");
        } else {
            KissMod.LOGGER.info("Cloth Config not detected");
        }
        registerRightClickEvent();
        registerKeyBinding();
        registerClientNetworkReceiver();
        registerCommands();
        KissModConfig.loadConfig();
        System.out.println("KissModClient initialized!");
    }
    private void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(literal("kissmod-rightclick")
                        .executes(context -> {
                            KissModConfig.rightClickEnabled = !KissModConfig.rightClickEnabled;
                            KissModConfig.saveConfig();
                            String translationKey = KissModConfig.rightClickEnabled ? "kiss-mod.toggle.enabled" : "kiss-mod.toggle.disabled";
                            context.getSource().sendFeedback(Text.translatable(translationKey));
                            return 1;
                        })
                        .then(argument("state", BoolArgumentType.bool())
                                .executes(context -> {
                                    boolean state = BoolArgumentType.getBool(context, "state");
                                    KissModConfig.rightClickEnabled = state;
                                    KissModConfig.saveConfig();
                                    String translationKey = state ? "kiss-mod.toggle.enabled" : "kiss-mod.toggle.disabled";
                                    context.getSource().sendFeedback(Text.translatable(translationKey));
                                    return 1;
                                })
                        )
                )
        );
    }
    private void registerRightClickEvent() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            long handle = client.getWindow().getHandle();
            boolean isRightClickPressed =
                    GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
            long currentTime = System.currentTimeMillis();

            if (KissModConfig.rightClickEnabled &&
                    client.player != null &&
                    client.world != null &&
                    client.currentScreen == null &&
                    client.options.sneakKey.isPressed()) {
                if (isRightClickPressed && (!wasRightClickPressed || currentTime - lastRightClickTriggerTime >= TRIGGER_INTERVAL)) {
                    Entity target = getEntityFromCameraRaycast(client);
                    if (target != null) {
                        if (KissModConfig.debugLogging) {
                            LOGGER.info("客户端发送数据包 右键 {}", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
                        }
                        sendKissPacket(target);
                        triggerEffect(target, client.world);
                        client.player.swingHand(Hand.MAIN_HAND);
                        lastRightClickTriggerTime = currentTime;
                    }
                }
            }
            wasRightClickPressed = isRightClickPressed;
        });
    }
    @Nullable
    private static Entity getEntityFromCameraRaycast(MinecraftClient client) {

        Entity cameraEntity = client.getCameraEntity();
        if (cameraEntity == null || client.world == null) return null;

        Vec3d eyePos = cameraEntity.getCameraPosVec(1.0f);
        Vec3d rotVec = cameraEntity.getRotationVec(1.0f);

        double reach = (client.player != null) ? client.player.getEntityInteractionRange() : 3.0;
        Vec3d endPos = eyePos.add(rotVec.multiply(reach));

        Box searchBox = new Box(eyePos, endPos).expand(1.0, 1.0, 1.0);

        Entity closestEntity = null;
        double closestDistSq = Double.MAX_VALUE;

        for (Entity e : client.world.getOtherEntities(cameraEntity, searchBox,
                entity -> !entity.isSpectator() && entity.isAlive())) {

            Box entityBox = e.getBoundingBox().expand(0.3);
            Optional<Vec3d> hitPoint = entityBox.raycast(eyePos, endPos);

            if (hitPoint.isPresent()) {
                double distSq = eyePos.squaredDistanceTo(hitPoint.get());
                if (distSq < closestDistSq) {
                    closestDistSq = distSq;
                    closestEntity = e;
                }
            }
        }

        return closestEntity;
    }

    private void sendKissPacket(Entity target) {
        UUID senderUuid = null;
        if (MinecraftClient.getInstance().player != null) {
            senderUuid = MinecraftClient.getInstance().player.getUuid();
        }
        if (KissModConfig.showOwnKiss) {
            ClientPlayNetworking.send(new KissC2SPacket(target.getUuid(), senderUuid));
        }
    }
    //? if >=1.21.9{
    /*public static final KeyBinding.Category KISS_MOD_CATEGORY = KeyBinding.Category.create(
            Identifier.of("kiss-mod", "keybindings")
    );
    *///?}
    private void registerKeyBinding() {
        kissKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.kiss-mod.kiss",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F7,
                //? if >=1.21.9{
                /*KISS_MOD_CATEGORY
                *///?} else{
                "key.category.kiss-mod.keybindings"
                //?}
        ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            boolean isKeyPressed = kissKey.isPressed();
            long currentTime = System.currentTimeMillis();

            if (isKeyPressed && (!wasKeyPressed || (currentTime - lastTriggerTime >= TRIGGER_INTERVAL))) {
                Entity target = getEntityFromCameraRaycast(client);
                if (target != null) {
                    if (KissModConfig.debugLogging) {
                        LOGGER.info("客户端发送数据包 按键 {}", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
                    }
                    sendKissPacket(target);
                    if (client.world != null) {
                        triggerEffect(target, client.world);
                    }
                    if (client.player != null) {
                        client.player.swingHand(Hand.MAIN_HAND);
                    }
                }
                lastTriggerTime = currentTime;
            }
            wasKeyPressed = isKeyPressed;
        });
    }
    private void registerClientNetworkReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(KissS2CPacket.TYPE, (payload, context) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            ClientWorld world = client.world;

            if (world == null || client.player == null) return;
            if (!KissModConfig.showOthersKiss) return;
            if (client.player.getUuid().equals(payload.getWhoPattedUuid())) return;
            UUID targetUuid = payload.getPattedEntityUuid();
            for (Entity entity : world.getEntities()) {
                if (entity.getUuid().equals(targetUuid)) {
                    if (KissModConfig.debugLogging) {
                        LOGGER.info("接收到了来自服务器的数据包 {}", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
                    }
                    triggerEffect(entity, world);
                    break;
                }
            }
        });
    }

    public static void triggerEffect(Entity target, World world) {
        if (world.isClient()) {
            spawnHeartParticles(world, target);
            if (KissModConfig.debugLogging) {
                LOGGER.info("生成粒子 at {}", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
            }
            if (KissModConfig.soundEnabled) {
                if (KissModConfig.debugLogging) {
                    LOGGER.info("播放声音 at {}", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
                }
                SoundEvent[] soundEvents = {
                        KissMod.CUSTOM_SOUND_EVENT,
                        KissMod.CUSTOM_SOUND1_EVENT,
                        KissMod.CUSTOM_SOUND2_EVENT};
                SoundEvent randomSound = soundEvents[new Random().nextInt(soundEvents.length)];

                world.playSound(
                        MinecraftClient.getInstance().player,
                        target.getX(), target.getY(), target.getZ(),
                        randomSound,
                        SoundCategory.PLAYERS,
                        (float) KissModConfig.soundVolume,
                        (float) KissModConfig.soundPitch
                );
            }
        }
    }

    public static void spawnHeartParticles(World world, Entity entity) {
        double x = entity.getX() + KissModConfig.centerOffsetX;
        double y = entity.getY() + entity.getHeight() + KissModConfig.centerOffsetY;
        double z = entity.getZ() + KissModConfig.centerOffsetZ;

        for (int i = 0; i < KissModConfig.particleCount; i++) {
            double offsetX = world.random.nextDouble() * (KissModConfig.maxOffsetX * 2) - KissModConfig.maxOffsetX;
            double offsetY = world.random.nextDouble() * (KissModConfig.maxOffsetY * 2) - KissModConfig.maxOffsetY;
            double offsetZ = world.random.nextDouble() * (KissModConfig.maxOffsetZ * 2) - KissModConfig.maxOffsetZ;
            //? if >=1.21.5 {
            /*world.addParticleClient(
                    ParticleTypes.HEART,
                    x + offsetX, y + offsetY, z + offsetZ,
                    0.0, 0.0, 0.0
            );
            *///?} else{
            world.addParticle(
                    ParticleTypes.HEART,
                    x + offsetX, y + offsetY, z + offsetZ,
                    0.0, 0.0, 0.0
            );//? }
        }
    }
}
