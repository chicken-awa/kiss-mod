package com.awa.kissmod.client;

import com.awa.kissmod.KissModConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class KissModConfigScreen {
    public static Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("title.kissmod.config"))
                .setSavingRunnable(KissModConfig::saveConfig);

        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("category.kissmod.general"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.kissmod.rightClickEnabled"), KissModConfig.rightClickEnabled)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("tooltip.kissmod.rightClickEnabled"))
                .setSaveConsumer(state -> KissModConfig.rightClickEnabled = state)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.kissmod.showOwnKiss"), KissModConfig.showOwnKiss)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("tooltip.kissmod.showOwnKiss"))
                .setSaveConsumer(state -> KissModConfig.showOwnKiss = state)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.kissmod.showOthersKiss"), KissModConfig.showOthersKiss)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("tooltip.kissmod.showOthersKiss"))
                .setSaveConsumer(state -> KissModConfig.showOthersKiss = state)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.kissmod.soundEnabled"), KissModConfig.soundEnabled)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("tooltip.kissmod.soundEnabled"))
                .setSaveConsumer(state -> KissModConfig.soundEnabled = state)
                .build());

        general.addEntry(entryBuilder.startIntField(Text.translatable("option.kissmod.particleCount"), KissModConfig.particleCount)
                .setDefaultValue(10)
                .setMin(0)
                .setMax(100)
                .setTooltip(Text.translatable("tooltip.kissmod.particleCount"))
                .setSaveConsumer(state -> KissModConfig.particleCount = state)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.kissmod.debugLogging"), KissModConfig.debugLogging)
                .setDefaultValue(false)
                .setTooltip(Text.translatable("tooltip.kissmod.debugLogging"))
                .setSaveConsumer(state -> KissModConfig.debugLogging = state)
                .build());

        ConfigCategory particle = builder.getOrCreateCategory(Text.translatable("category.kissmod.particle"));

        particle.addEntry(entryBuilder.startDoubleField(Text.translatable("option.kissmod.centerOffsetX"), KissModConfig.centerOffsetX)
                .setDefaultValue(0.0)
                .setTooltip(Text.translatable("tooltip.kissmod.centerOffsetX"))
                .setSaveConsumer(state -> KissModConfig.centerOffsetX = state)
                .build());

        particle.addEntry(entryBuilder.startDoubleField(Text.translatable("option.kissmod.centerOffsetY"), KissModConfig.centerOffsetY)
                .setDefaultValue(0.0)
                .setTooltip(Text.translatable("tooltip.kissmod.centerOffsetY"))
                .setSaveConsumer(state -> KissModConfig.centerOffsetY = state)
                .build());

        particle.addEntry(entryBuilder.startDoubleField(Text.translatable("option.kissmod.centerOffsetZ"), KissModConfig.centerOffsetZ)
                .setDefaultValue(0.0)
                .setTooltip(Text.translatable("tooltip.kissmod.centerOffsetZ"))
                .setSaveConsumer(state -> KissModConfig.centerOffsetZ = state)
                .build());

        particle.addEntry(entryBuilder.startDoubleField(Text.translatable("option.kissmod.maxOffsetX"), KissModConfig.maxOffsetX)
                .setDefaultValue(0.5)
                .setMin(0.0)
                .setTooltip(Text.translatable("tooltip.kissmod.maxOffsetX"))
                .setSaveConsumer(state -> KissModConfig.maxOffsetX = state)
                .build());

        particle.addEntry(entryBuilder.startDoubleField(Text.translatable("option.kissmod.maxOffsetY"), KissModConfig.maxOffsetY)
                .setDefaultValue(0.5)
                .setMin(0.0)
                .setTooltip(Text.translatable("tooltip.kissmod.maxOffsetY"))
                .setSaveConsumer(state -> KissModConfig.maxOffsetY = state)
                .build());

        particle.addEntry(entryBuilder.startDoubleField(Text.translatable("option.kissmod.maxOffsetZ"), KissModConfig.maxOffsetZ)
                .setDefaultValue(0.5)
                .setMin(0.0)
                .setTooltip(Text.translatable("tooltip.kissmod.maxOffsetZ"))
                .setSaveConsumer(state -> KissModConfig.maxOffsetZ = state)
                .build());

        return builder.build();
    }
}