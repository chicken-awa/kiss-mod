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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
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
    public static boolean rightClickEnabled = KissModConfig.loadConfig();
    private static KeyBinding kissKey;
    private static boolean wasKeyPressed = false;
    private static long lastTriggerTime = 0;
    private static final long TRIGGER_INTERVAL = 175;
    private static final Logger LOGGER = KissMod.LOGGER;
    @Override
    public void onInitializeClient() {
        registerRightClickEvent();
        registerKeyBinding();
        registerClientNetworkReceiver();
        registerCommands();
        System.out.println("KissModClient initialized!");
    }
    private void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(literal("kissmod-rightclick")
                        .executes(context -> {
                            rightClickEnabled = !rightClickEnabled;
                            KissModConfig.saveConfig(rightClickEnabled);
                            String translationKey = rightClickEnabled ? "kiss-mod.toggle.enabled" : "kiss-mod.toggle.disabled";
                            context.getSource().sendFeedback(Text.translatable(translationKey));
                            return 1;
                        })
                        .then(argument("state", BoolArgumentType.bool())
                                .executes(context -> {
                                    boolean state = BoolArgumentType.getBool(context, "state");
                                    rightClickEnabled = state;
                                    KissModConfig.saveConfig(state);
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
            if (!rightClickEnabled || !world.isClient()) return ActionResult.PASS;
            Entity target = MinecraftClient.getInstance().targetedEntity;
            if (player.isSneaking()&& entity != null) {
                LOGGER.info("PLAY 客户端发送数据包 右键 {}", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));

                if (target != null) {
                    UUID senderUuid = null;
                    if (MinecraftClient.getInstance().player != null) {
                        senderUuid = MinecraftClient.getInstance().player.getUuid();
                    }
                    ClientPlayNetworking.send(new KissC2SPacket(target.getUuid(), senderUuid));
                    triggerEffect(target, world);
                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.PASS;
        });
    }
    private void registerKeyBinding() {
        kissKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.kiss-mod.kiss", // 键绑 ID
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F7,
                "category.kiss-mod.keybindings" // 键绑分类
        ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            boolean isKeyPressed = kissKey.isPressed();
            long currentTime = System.currentTimeMillis();

            if (isKeyPressed && (!wasKeyPressed || (currentTime - lastTriggerTime >= TRIGGER_INTERVAL))) {
                Entity target = MinecraftClient.getInstance().targetedEntity;
                if (target != null) {
                    LOGGER.info("PLAY 客户端发送数据包 按键");
                    UUID senderUuid = null;
                    if (MinecraftClient.getInstance().player != null) {
                        senderUuid = MinecraftClient.getInstance().player.getUuid();
                    }
                    ClientPlayNetworking.send(new KissC2SPacket(target.getUuid(), senderUuid));
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
                for (Entity entity : world.getEntities()) {
                    if (entity.getUuid().equals(payload.getPattedEntityUuid())) {
                        if (MinecraftClient.getInstance().player != null && !MinecraftClient.getInstance().player.getUuid().equals(payload.getWhoPattedUuid())){
                            LOGGER.info("PLAY 接收到了来自服务器的数据包");
                            triggerEffect(entity, world);
                            break;
                         }
                    }
                }
            }
        });
    }

    public static void triggerEffect(Entity target, World world) {
        if (world.isClient) {
            spawnHeartParticles(world, target);
            LOGGER.info("PLAY 生成粒子 播放声音 at {}", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
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

    public static void spawnHeartParticles(World world, Entity entity) {
        if (world.isClient) {
            double x = entity.getX();
            double y = entity.getY() + entity.getHeight();
            double z = entity.getZ();

            for (int i = 0; i < 20; i++) {
                double offsetX = world.random.nextDouble() - 0.5;
                double offsetY = world.random.nextDouble() - 0.5;
                double offsetZ = world.random.nextDouble() - 0.5;
                world.addParticle(
                        net.minecraft.particle.ParticleTypes.HEART,
                        x + offsetX, y + offsetY, z + offsetZ,
                        0.0, 0.0, 0.0
                );
            }
        }
    }
}
