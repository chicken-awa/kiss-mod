package com.awa.kissmod.client;

import com.awa.kissmod.KissMod;
import com.awa.kissmod.KissModConfig;
import com.awa.kissmod.packet.KissC2SPacket;
import com.awa.kissmod.packet.KissS2CPacket;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
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
import net.minecraft.world.World;
import com.mojang.brigadier.arguments.BoolArgumentType;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;


import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

public class KissModClient implements ClientModInitializer {

    private static KeyBinding kissKey;
    private static boolean wasKeyPressed = false;
    private static long lastTriggerTime = 0;
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
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!KissModConfig.rightClickEnabled || !world.isClient()) return ActionResult.PASS;
            Entity target = MinecraftClient.getInstance().targetedEntity;
            if (player.isSneaking()&& entity != null) {
                if (KissModConfig.debugLogging) {
                    LOGGER.info("客户端发送数据包 右键 {}", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
                }
                if (target != null) {
                    sendKissPacket(target);
                    triggerEffect(target, world);
                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.PASS;
        });
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
                Entity target = MinecraftClient.getInstance().targetedEntity;
                if (target != null) {
                    if (KissModConfig.debugLogging) {
                        LOGGER.info("客户端发送数据包 按键 {}", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
                    }
                    sendKissPacket(target);
                    if (client.world != null) {
                        triggerEffect(target, client.world);
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

            if (world != null) {
                UUID targetUuid = payload.getPattedEntityUuid();
                for (Entity entity : world.getEntities()) {
                    if (entity.getUuid().equals(targetUuid)) {
                        if (client.player != null && !client.player.getUuid().equals(payload.getWhoPattedUuid())) {
                            if (KissModConfig.debugLogging) {
                                LOGGER.info("接收到了来自服务器的数据包 {}", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
                            }
                            if(KissModConfig.showOthersKiss) {
                                triggerEffect(entity, world);
                            }
                        }
                        break;
                    }
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
                        1.0F, 1.0F
                );
            }
        }
    }

    public static void spawnHeartParticles(World world, Entity entity) {
        double x = entity.getX();
        double y = entity.getY() + entity.getHeight();
        double z = entity.getZ();

        for (int i = 0; i < KissModConfig.particleCount; i++) {
            double offsetX = world.random.nextDouble() - 0.5;
            double offsetY = world.random.nextDouble() - 0.5;
            double offsetZ = world.random.nextDouble() - 0.5;
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
