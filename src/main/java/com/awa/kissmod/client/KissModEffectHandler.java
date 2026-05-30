package com.awa.kissmod.client;

import com.awa.kissmod.KissMod;
import com.awa.kissmod.KissModConfig;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class KissModEffectHandler {

    public static void triggerEffect(Entity target, Level world) {
        if (world.isClientSide()) {
            spawnHeartParticles(world, target);
            if (KissModConfig.debugLogging) {
                KissMod.LOGGER.info("生成粒子 at {}", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
            }
            if (KissModConfig.soundEnabled) {
                if (KissModConfig.debugLogging) {
                    KissMod.LOGGER.info("播放声音 at {}", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
                }
                SoundEvent[] soundEvents = {
                        KissMod.CUSTOM_SOUND_EVENT.get(),
                        KissMod.CUSTOM_SOUND1_EVENT.get(),
                        KissMod.CUSTOM_SOUND2_EVENT.get()};
                SoundEvent randomSound = soundEvents[new Random().nextInt(soundEvents.length)];

                world.playSound(
                        Minecraft.getInstance().player,
                        target.getX(), target.getY(), target.getZ(),
                        randomSound,
                        SoundSource.PLAYERS,
                        (float) KissModConfig.soundVolume,
                        (float) KissModConfig.soundPitch
                );
            }
        }
    }

    public static void spawnHeartParticles(Level world, Entity entity) {
        double x = entity.getX() + KissModConfig.centerOffsetX;
        double y = entity.getY() + entity.getBbHeight() + KissModConfig.centerOffsetY;
        double z = entity.getZ() + KissModConfig.centerOffsetZ;

        for (int i = 0; i < KissModConfig.particleCount; i++) {
            double offsetX = world.random.nextDouble() * (KissModConfig.maxOffsetX * 2) - KissModConfig.maxOffsetX;
            double offsetY = world.random.nextDouble() * (KissModConfig.maxOffsetY * 2) - KissModConfig.maxOffsetY;
            double offsetZ = world.random.nextDouble() * (KissModConfig.maxOffsetZ * 2) - KissModConfig.maxOffsetZ;
            //? if >=1.21.5 {
            /*world.addParticle(
                    ParticleTypes.HEART,
                    true,
                    false,
                    x + offsetX, y + offsetY, z + offsetZ,
                    0.0, 0.0, 0.0
            );
            *///? } else {
            world.addAlwaysVisibleParticle(
                    ParticleTypes.HEART,
                    true,
                    x + offsetX, y + offsetY, z + offsetZ,
                    0.0, 0.0, 0.0
            );//? }
        }
    }
}
