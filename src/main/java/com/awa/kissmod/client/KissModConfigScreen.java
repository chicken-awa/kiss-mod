package com.awa.kissmod.client;

import com.awa.kissmod.KissModConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class KissModConfigScreen {
    public static Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("title.kissmod.config"))
                .setSavingRunnable(KissModConfig::saveConfig);

        ConfigCategory general = builder.getOrCreateCategory(Component.translatable("category.kissmod.general"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.kissmod.rightClickEnabled"), KissModConfig.rightClickEnabled)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("tooltip.kissmod.rightClickEnabled"))
                .setSaveConsumer(state -> KissModConfig.rightClickEnabled = state)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.kissmod.showOwnKiss"), KissModConfig.showOwnKiss)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("tooltip.kissmod.showOwnKiss"))
                .setSaveConsumer(state -> KissModConfig.showOwnKiss = state)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.kissmod.showOthersKiss"), KissModConfig.showOthersKiss)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("tooltip.kissmod.showOthersKiss"))
                .setSaveConsumer(state -> KissModConfig.showOthersKiss = state)
                .build());

        general.addEntry(entryBuilder.startIntField(Component.translatable("option.kissmod.triggerCooldown"), KissModConfig.triggerCooldown)
                .setDefaultValue(175)
                .setMin(0)
                .setTooltip(Component.translatable("tooltip.kissmod.triggerCooldown"))
                .setSaveConsumer(state -> KissModConfig.triggerCooldown = state)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.kissmod.debugLogging"), KissModConfig.debugLogging)
                .setDefaultValue(false)
                .setTooltip(Component.translatable("tooltip.kissmod.debugLogging"))
                .setSaveConsumer(state -> KissModConfig.debugLogging = state)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.kissmod.proxLibEnabled"), KissModConfig.proxLibEnabled)
                .setDefaultValue(false)
                .setTooltip(Component.translatable("tooltip.kissmod.proxLibEnabled"))
                .setSaveConsumer(state -> KissModConfig.proxLibEnabled = state)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.kissmod.proxLibWhitelistMode"), KissModConfig.proxLibWhitelistMode)
                .setDefaultValue(false)
                .setTooltip(Component.translatable("tooltip.kissmod.proxLibWhitelistMode"))
                .setSaveConsumer(state -> KissModConfig.proxLibWhitelistMode = state)
                .build());

        general.addEntry(entryBuilder.startStrList(Component.translatable("option.kissmod.proxLibServerList"), KissModConfig.proxLibServerList)
                .setDefaultValue(new java.util.ArrayList<>())
                .setTooltip(Component.translatable("tooltip.kissmod.proxLibServerList"))
                .setSaveConsumer(state -> KissModConfig.proxLibServerList = state)
                .build());

        ConfigCategory sound = builder.getOrCreateCategory(Component.translatable("category.kissmod.sound"));

        sound.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.kissmod.soundEnabled"), KissModConfig.soundEnabled)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("tooltip.kissmod.soundEnabled"))
                .setSaveConsumer(state -> KissModConfig.soundEnabled = state)
                .build());

        sound.addEntry(entryBuilder.startDoubleField(Component.translatable("option.kissmod.soundVolume"), KissModConfig.soundVolume)
                .setDefaultValue(1.0)
                .setMin(0.0)
                .setMax(1.0)
                .setTooltip(Component.translatable("tooltip.kissmod.soundVolume"))
                .setSaveConsumer(state -> KissModConfig.soundVolume = state)
                .build());

        sound.addEntry(entryBuilder.startDoubleField(Component.translatable("option.kissmod.soundPitch"), KissModConfig.soundPitch)
                .setDefaultValue(1.0)
                .setMin(0.5)
                .setMax(2.0)
                .setTooltip(Component.translatable("tooltip.kissmod.soundPitch"))
                .setSaveConsumer(state -> KissModConfig.soundPitch = state)
                .build());

        ConfigCategory particle = builder.getOrCreateCategory(Component.translatable("category.kissmod.particle"));

        particle.addEntry(entryBuilder.startIntField(Component.translatable("option.kissmod.particleCount"), KissModConfig.particleCount)
                .setDefaultValue(10)
                .setMin(0)
                .setMax(100)
                .setTooltip(Component.translatable("tooltip.kissmod.particleCount"))
                .setSaveConsumer(state -> KissModConfig.particleCount = state)
                .build());

        particle.addEntry(entryBuilder.startDoubleField(Component.translatable("option.kissmod.centerOffsetX"), KissModConfig.centerOffsetX)
                .setDefaultValue(0.0)
                .setTooltip(Component.translatable("tooltip.kissmod.centerOffsetX"))
                .setSaveConsumer(state -> KissModConfig.centerOffsetX = state)
                .build());

        particle.addEntry(entryBuilder.startDoubleField(Component.translatable("option.kissmod.centerOffsetY"), KissModConfig.centerOffsetY)
                .setDefaultValue(0.0)
                .setTooltip(Component.translatable("tooltip.kissmod.centerOffsetY"))
                .setSaveConsumer(state -> KissModConfig.centerOffsetY = state)
                .build());

        particle.addEntry(entryBuilder.startDoubleField(Component.translatable("option.kissmod.centerOffsetZ"), KissModConfig.centerOffsetZ)
                .setDefaultValue(0.0)
                .setTooltip(Component.translatable("tooltip.kissmod.centerOffsetZ"))
                .setSaveConsumer(state -> KissModConfig.centerOffsetZ = state)
                .build());

        particle.addEntry(entryBuilder.startDoubleField(Component.translatable("option.kissmod.maxOffsetX"), KissModConfig.maxOffsetX)
                .setDefaultValue(0.5)
                .setMin(0.0)
                .setTooltip(Component.translatable("tooltip.kissmod.maxOffsetX"))
                .setSaveConsumer(state -> KissModConfig.maxOffsetX = state)
                .build());

        particle.addEntry(entryBuilder.startDoubleField(Component.translatable("option.kissmod.maxOffsetY"), KissModConfig.maxOffsetY)
                .setDefaultValue(0.5)
                .setMin(0.0)
                .setTooltip(Component.translatable("tooltip.kissmod.maxOffsetY"))
                .setSaveConsumer(state -> KissModConfig.maxOffsetY = state)
                .build());

        particle.addEntry(entryBuilder.startDoubleField(Component.translatable("option.kissmod.maxOffsetZ"), KissModConfig.maxOffsetZ)
                .setDefaultValue(0.5)
                .setMin(0.0)
                .setTooltip(Component.translatable("tooltip.kissmod.maxOffsetZ"))
                .setSaveConsumer(state -> KissModConfig.maxOffsetZ = state)
                .build());

        return builder.build();
    }
}