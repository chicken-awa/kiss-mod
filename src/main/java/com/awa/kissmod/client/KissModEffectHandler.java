package com.awa.kissmod.client;

import com.awa.kissmod.KissMod;
import com.awa.kissmod.KissModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class KissModEffectHandler {

    public static void triggerEffect(Entity target, World world) {
        if (world.isClient()) {
            spawnHeartParticles(world, target);
            if (KissModConfig.debugLogging) {
                KissMod.LOGGER.info("生成粒子 at {}", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
            }
            if (KissModConfig.soundEnabled) {
                if (KissModConfig.debugLogging) {
                    KissMod.LOGGER.info("播放声音 at {}", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
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
            world.addImportantParticle(
                    ParticleTypes.HEART,
                    true,
                    x + offsetX, y + offsetY, z + offsetZ,
                    0.0, 0.0, 0.0
            );
        }
    }
}
