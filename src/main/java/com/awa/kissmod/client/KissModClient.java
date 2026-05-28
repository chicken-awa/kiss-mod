package com.awa.kissmod.client;

import com.awa.kissmod.KissMod;
import com.awa.kissmod.KissModConfig;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.util.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
public class KissModClient {

    private static KeyMapping kissKey;
    private static boolean wasKeyPressed = false;
    private static boolean wasRightClickPressed = false;
    private static long lastTriggerTime = 0;
    private static long lastRightClickTriggerTime = 0;
    private static final Logger LOGGER = KissMod.LOGGER;

    public static void init() {
        if (ModList.get().isLoaded("cloth-config")) {
            KissMod.LOGGER.info("Cloth Config detected");
        } else {
            KissMod.LOGGER.info("Cloth Config not detected");
        }

        registerKeyBinding();

        KissModNetworkHandler.registerHandlers();
        KissModConfig.loadConfig();
        System.out.println("KissModClient initialized!");
    }

    @SubscribeEvent
    public static void onRightClickTick(ClientTickEvent.Post event) {
        Minecraft client = Minecraft.getInstance();
        long handle = client.getWindow().getWindow();
        boolean isRightClickPressed =
                GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
        long currentTime = System.currentTimeMillis();

        if (KissModConfig.rightClickEnabled &&
                client.player != null &&
                client.level != null &&
                client.screen == null &&
                client.options.keyShift.isDown()) {
            if (isRightClickPressed && (!wasRightClickPressed || currentTime - lastRightClickTriggerTime >= KissModConfig.triggerCooldown)) {
                Entity target = getEntityFromCameraRaycast(client);
                if (target != null) {
                    if (KissModConfig.debugLogging) {
                        LOGGER.info("客户端将发送数据包 右键 {}", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
                    }
                    KissModNetworkHandler.sendKissPacket(target);
                    KissModEffectHandler.triggerEffect(target, client.level);
                    client.player.swing(InteractionHand.MAIN_HAND);
                    lastRightClickTriggerTime = currentTime;
                }
            }
        }
        wasRightClickPressed = isRightClickPressed;
    }
    @Nullable
    private static Entity getEntityFromCameraRaycast(Minecraft client) {

        Entity cameraEntity = client.getCameraEntity();
        if (cameraEntity == null || client.level == null) return null;

        Vec3 eyePos = cameraEntity.getEyePosition(1.0f);
        Vec3 rotVec = cameraEntity.getViewVector(1.0f);

        double reach = (client.player != null) ? client.player.entityInteractionRange() : 3.0;
        Vec3 endPos = eyePos.add(rotVec.scale(reach));

        AABB searchBox = new AABB(eyePos, endPos).inflate(1.0, 1.0, 1.0);

        Entity closestEntity = null;
        double closestDistSq = Double.MAX_VALUE;

        for (Entity e : client.level.getEntities(cameraEntity, searchBox,
                entity -> !entity.isSpectator() && entity.isAlive())) {

            AABB entityBox = e.getBoundingBox();
            Optional<Vec3> hitPoint = entityBox.clip(eyePos, endPos);

            if (hitPoint.isPresent()) {
                double distSq = eyePos.distanceToSqr(hitPoint.get());
                if (distSq < closestDistSq) {
                    closestDistSq = distSq;
                    closestEntity = e;
                }
            }
        }
        return closestEntity;
    }

    @SubscribeEvent
    public static void onKeyTriggerTick(ClientTickEvent.Post event) {
        Minecraft client = Minecraft.getInstance();
        boolean isKeyPressed = kissKey.isDown();
        long currentTime = System.currentTimeMillis();

        if (isKeyPressed && (!wasKeyPressed || (currentTime - lastTriggerTime >= KissModConfig.triggerCooldown))) {
            Entity target = getEntityFromCameraRaycast(client);
            if (target != null) {
                if (KissModConfig.debugLogging) {
                    LOGGER.info("客户端将发送数据包 按键 {}", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
                }
                KissModNetworkHandler.sendKissPacket(target);
                if (client.level != null) {
                    KissModEffectHandler.triggerEffect(target, client.level);
                }
                if (client.player != null) {
                    client.player.swing(InteractionHand.MAIN_HAND);
                }
            }
            lastTriggerTime = currentTime;
        }
        wasKeyPressed = isKeyPressed;
    }
    @EventBusSubscriber(modid = KissMod.MOD_ID, value = Dist.CLIENT)
    public static class ModBusEvents {
        @SubscribeEvent
        public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
            event.register(kissKey);
        }
    }
    //? if >=1.21.9{
    /*public static final KeyMapping.Category KISS_MOD_CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath("kiss-mod", "keybindings")
    );
    *///?}
    private static void registerKeyBinding() {
        kissKey = new KeyMapping(
                "key.kiss-mod.kiss",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_F7,
                //? if >=1.21.9{
                /*KISS_MOD_CATEGORY
                 *///?} else{
                "key.category.kiss-mod.keybindings"
                //?}
        );
    }
}
