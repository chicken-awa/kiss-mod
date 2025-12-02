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
                .setSavingRunnable(() -> KissModConfig.saveConfig(
                        KissModConfig.rightClickEnabled,
                        KissModConfig.soundEnabled,
                        KissModConfig.particleCount,
                        KissModConfig.debugLogging
                ));

        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("category.kissmod.general"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.kissmod.rightClickEnabled"), KissModConfig.rightClickEnabled)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("tooltip.kissmod.rightClickEnabled"))
                .setSaveConsumer(state -> KissModConfig.rightClickEnabled = state)
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

        return builder.build();
    }
}