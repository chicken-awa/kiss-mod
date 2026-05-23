package com.awa.kissmod.client;

import com.awa.kissmod.KissMod;
import com.awa.kissmod.KissModConfig;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.*;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
public class KissModClient implements ClientModInitializer {

    private static KeyBinding kissKey;
    private static boolean wasKeyPressed = false;
    private static boolean wasRightClickPressed = false;
    private static long lastTriggerTime = 0;
    private static long lastRightClickTriggerTime = 0;
    private static final Logger LOGGER = KissMod.LOGGER;

    @Override
    public void onInitializeClient() {
        if (FabricLoader.getInstance().isModLoaded("cloth-config")) {
            KissMod.LOGGER.info("Cloth Config detected");
        } else {
            KissMod.LOGGER.info("Cloth Config not detected");
        }

        registerKeyBinding();

        registerRightClickEvent();
        registerKeyTriggerEvent();

        KissModNetworkHandler.registerHandlers();
        KissModCommandRegistration.registerCommands();
        KissModConfig.loadConfig();
        System.out.println("KissModClient initialized!");
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
                if (isRightClickPressed && (!wasRightClickPressed || currentTime - lastRightClickTriggerTime >= KissModConfig.triggerCooldown)) {
                    Entity target = getEntityFromCameraRaycast(client);
                    if (target != null) {
                        if (KissModConfig.debugLogging) {
                            LOGGER.info("客户端将发送数据包 右键 {}", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
                        }
                        KissModNetworkHandler.sendKissPacket(target);
                        KissModEffectHandler.triggerEffect(target, client.world);
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

        double reach = (client.player != null && client.player.isCreative()) ? 5.0 : 3.0;
        Vec3d endPos = eyePos.add(rotVec.multiply(reach));

        Box searchBox = new Box(eyePos, endPos).expand(1.0, 1.0, 1.0);

        Entity closestEntity = null;
        double closestDistSq = Double.MAX_VALUE;

        for (Entity e : client.world.getOtherEntities(cameraEntity, searchBox,
                entity -> !entity.isSpectator() && entity.isAlive())) {

            Box entityBox = e.getBoundingBox();
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
    private void registerKeyTriggerEvent() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            boolean isKeyPressed = kissKey.isPressed();
            long currentTime = System.currentTimeMillis();

            if (isKeyPressed && (!wasKeyPressed || (currentTime - lastTriggerTime >= KissModConfig.triggerCooldown))) {
                Entity target = getEntityFromCameraRaycast(client);
                if (target != null) {
                    if (KissModConfig.debugLogging) {
                        LOGGER.info("客户端将发送数据包 按键 {}", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
                    }
                    KissModNetworkHandler.sendKissPacket(target);
                    if (client.world != null) {
                        KissModEffectHandler.triggerEffect(target, client.world);
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
    private void registerKeyBinding() {
        kissKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.kiss-mod.kiss",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F7,
                "key.category.kiss-mod.keybindings"
        ));
    }
}
