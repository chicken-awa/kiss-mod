package com.awa.kissmod;

import net.minecraft.client.gui.screen.Screen;

import java.io.*;
import java.util.Properties;

public class KissModConfig {
    private static final String CONFIG_FILE = "config/kissmod.properties";
    private static final String RIGHT_CLICK_KEY = "rightClickEnabled";
    private static final String SOUND_ENABLED_KEY = "soundEnabled";
    private static final String PARTICLE_COUNT_KEY = "particleCount";
    private static final String DEBUG_LOGGING_KEY = "debugLogging";

    public static boolean soundEnabled = true;
    public static int particleCount = 20;
    public static boolean debugLogging = false;

    public static boolean loadConfig() {
        File configFile = new File(CONFIG_FILE);
        if (!configFile.exists()) {
            soundEnabled = true;
            particleCount = 20;
            debugLogging = false;
            return true;
        }
        try (InputStream input = new FileInputStream(configFile)) {
            Properties prop = new Properties();
            prop.load(input);
            soundEnabled = Boolean.parseBoolean(prop.getProperty(SOUND_ENABLED_KEY, "true"));
            debugLogging = Boolean.parseBoolean(prop.getProperty(DEBUG_LOGGING_KEY, "false"));
            try {
                particleCount = Integer.parseInt(prop.getProperty(PARTICLE_COUNT_KEY, "20"));
            } catch (NumberFormatException e) {
                KissMod.LOGGER.warn("粒子数量无效 ({})", prop.getProperty(PARTICLE_COUNT_KEY), e);
                particleCount = 20;
            }
            return Boolean.parseBoolean(prop.getProperty(RIGHT_CLICK_KEY, "true"));
        } catch (IOException e) {
            soundEnabled = true;
            particleCount = 20;
            debugLogging = false;
            return true;
        }
    }
    public static void saveConfig(boolean rightClickState, boolean soundState, int particleCountState, boolean debugLoggingState) {
        File configDir = new File("config");
        if (!configDir.exists()) {
            boolean created = configDir.mkdirs();
            if (!created) {
                KissMod.LOGGER.error("无法创建配置目录 `{}`", configDir.getAbsolutePath());
            }
        }
        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            Properties prop = new Properties();
            prop.setProperty(RIGHT_CLICK_KEY, String.valueOf(rightClickState));
            prop.setProperty(SOUND_ENABLED_KEY, String.valueOf(soundState));
            prop.setProperty(PARTICLE_COUNT_KEY, String.valueOf(particleCountState));
            prop.setProperty(DEBUG_LOGGING_KEY, String.valueOf(debugLoggingState));
            prop.store(output, "KissMod Configuration");
            updateClientConfig(rightClickState, soundState, particleCountState, debugLoggingState);
        } catch (IOException e) {
            KissMod.LOGGER.error("保存配置文件失败", e);
        }
    }
    private static void updateClientConfig(boolean rightClickState, boolean soundState, int particleCountState, boolean debugLoggingState){
        try {
            Class<?> clientClass = Class.forName("com.awa.kissmod.client.KissModClient");

            java.lang.reflect.Field rightClickField = clientClass.getDeclaredField("rightClickEnabled");
            rightClickField.setAccessible(true);
            rightClickField.setBoolean(null, rightClickState);

            java.lang.reflect.Field soundEnabledField = clientClass.getDeclaredField("soundEnabled");
            soundEnabledField.setAccessible(true);
            soundEnabledField.setBoolean(null, soundState);

            java.lang.reflect.Field particleCountField = clientClass.getDeclaredField("particleCount");
            particleCountField.setAccessible(true);
            particleCountField.setInt(null, particleCountState);

            java.lang.reflect.Field debugLoggingField = clientClass.getDeclaredField("debugLogging");
            debugLoggingField.setAccessible(true);
            debugLoggingField.setBoolean(null, debugLoggingState);
        } catch (Exception e) {
            KissMod.LOGGER.warn("无法更新配置: {}", e.getMessage());
        }
    }
    public static Screen getScreen(Screen parent) {
        try {
            Class<?> configBuilderClass = Class.forName("me.shedaniel.clothconfig2.api.ConfigBuilder");
            Object builder = configBuilderClass.getMethod("create")
                    .invoke(null);
            builder.getClass().getMethod("setParentScreen", Screen.class)
                    .invoke(builder, parent);
            builder.getClass().getMethod("setTitle", net.minecraft.text.Text.class)
                    .invoke(builder, net.minecraft.text.Text.translatable("title.kissmod.config"));
            builder.getClass().getMethod("setSavingRunnable", Runnable.class)
                    .invoke(builder, (Runnable) () -> {});
            Object entryBuilder = builder.getClass().getMethod("entryBuilder")
                    .invoke(builder);
            Object general = builder.getClass().getMethod("getOrCreateCategory", net.minecraft.text.Text.class)
                    .invoke(builder, net.minecraft.text.Text.translatable("category.kissmod.general"));

            final boolean[] rightClickHolder = {loadConfig()};
            final boolean[] soundEnabledHolder = {soundEnabled};
            final int[] particleCountHolder = {particleCount};
            final boolean[] debugLoggingHolder = {debugLogging};

            Object booleanToggle = entryBuilder.getClass().getMethod("startBooleanToggle", net.minecraft.text.Text.class, boolean.class)
                    .invoke(entryBuilder, net.minecraft.text.Text.translatable("option.kissmod.rightClickEnabled"), rightClickHolder[0]);
            booleanToggle.getClass().getMethod("setDefaultValue", boolean.class)
                    .invoke(booleanToggle, true);
            booleanToggle.getClass().getMethod("setTooltip", net.minecraft.text.Text[].class)
                    .invoke(booleanToggle, (Object) new net.minecraft.text.Text[]{
                            net.minecraft.text.Text.translatable("tooltip.kissmod.rightClickEnabled")
                    });
            booleanToggle.getClass().getMethod("setSaveConsumer", java.util.function.Consumer.class)
                    .invoke(booleanToggle, (java.util.function.Consumer<Boolean>) state -> {
                        rightClickHolder[0] = state;
                        saveConfig(rightClickHolder[0], soundEnabledHolder[0], particleCountHolder[0], debugLoggingHolder[0]);
                    });
            Object builtEntry = booleanToggle.getClass().getMethod("build")
                    .invoke(booleanToggle);
            Class<?> abstractEntryClass = Class.forName("me.shedaniel.clothconfig2.api.AbstractConfigListEntry");
            general.getClass().getMethod("addEntry", abstractEntryClass)
                    .invoke(general, builtEntry);

            Object soundToggle = entryBuilder.getClass().getMethod("startBooleanToggle", net.minecraft.text.Text.class, boolean.class)
                    .invoke(entryBuilder, net.minecraft.text.Text.translatable("option.kissmod.soundEnabled"), soundEnabledHolder[0]);
            soundToggle.getClass().getMethod("setDefaultValue", boolean.class)
                    .invoke(soundToggle, true);
            soundToggle.getClass().getMethod("setTooltip", net.minecraft.text.Text[].class)
                    .invoke(soundToggle, (Object) new net.minecraft.text.Text[]{
                            net.minecraft.text.Text.translatable("tooltip.kissmod.soundEnabled")
                    });
            soundToggle.getClass().getMethod("setSaveConsumer", java.util.function.Consumer.class)
                    .invoke(soundToggle, (java.util.function.Consumer<Boolean>) state -> {
                        soundEnabledHolder[0] = state;
                        saveConfig(rightClickHolder[0], soundEnabledHolder[0], particleCountHolder[0], debugLoggingHolder[0]);
                    });
            Object builtSoundEntry = soundToggle.getClass().getMethod("build")
                    .invoke(soundToggle);
            general.getClass().getMethod("addEntry", abstractEntryClass)
                    .invoke(general, builtSoundEntry);

            Object integerField = entryBuilder.getClass().getMethod("startIntField", net.minecraft.text.Text.class, int.class)
                    .invoke(entryBuilder, net.minecraft.text.Text.translatable("option.kissmod.particleCount"), particleCountHolder[0]);
            integerField.getClass().getMethod("setDefaultValue", int.class)
                    .invoke(integerField, 20);
            integerField.getClass().getMethod("setMin", int.class)
                    .invoke(integerField, 0);
            integerField.getClass().getMethod("setMax", int.class)
                    .invoke(integerField, 100);
            integerField.getClass().getMethod("setTooltip", net.minecraft.text.Text[].class)
                    .invoke(integerField, (Object) new net.minecraft.text.Text[]{
                            net.minecraft.text.Text.translatable("tooltip.kissmod.particleCount")
                    });
            integerField.getClass().getMethod("setSaveConsumer", java.util.function.Consumer.class)
                    .invoke(integerField, (java.util.function.Consumer<Integer>) state -> {
                        particleCountHolder[0] = state;
                        saveConfig(rightClickHolder[0], soundEnabledHolder[0], particleCountHolder[0], debugLoggingHolder[0]);
                    });
            Object builtIntegerField = integerField.getClass().getMethod("build")
                    .invoke(integerField);
            general.getClass().getMethod("addEntry", abstractEntryClass)
                    .invoke(general, builtIntegerField);


            Object debugLoggingToggle = entryBuilder.getClass().getMethod("startBooleanToggle", net.minecraft.text.Text.class, boolean.class)
                    .invoke(entryBuilder, net.minecraft.text.Text.translatable("option.kissmod.debugLogging"), debugLoggingHolder[0]);
            debugLoggingToggle.getClass().getMethod("setDefaultValue", boolean.class)
                    .invoke(debugLoggingToggle, false);
            debugLoggingToggle.getClass().getMethod("setTooltip", net.minecraft.text.Text[].class)
                    .invoke(debugLoggingToggle, (Object) new net.minecraft.text.Text[]{
                            net.minecraft.text.Text.translatable("tooltip.kissmod.debugLogging")
                    });
            debugLoggingToggle.getClass().getMethod("setSaveConsumer", java.util.function.Consumer.class)
                    .invoke(debugLoggingToggle, (java.util.function.Consumer<Boolean>) state -> {
                        debugLoggingHolder[0] = state;
                        saveConfig(rightClickHolder[0], soundEnabledHolder[0], particleCountHolder[0], debugLoggingHolder[0]);
                    });
            Object builtDebugLoggingEntry = debugLoggingToggle.getClass().getMethod("build")
                    .invoke(debugLoggingToggle);
            general.getClass().getMethod("addEntry", abstractEntryClass)
                    .invoke(general, builtDebugLoggingEntry);

            return (Screen) builder.getClass().getMethod("build")
                    .invoke(builder);
        } catch (Exception e) {
            KissMod.LOGGER.error("异常", e);
            return new net.minecraft.client.gui.screen.Screen(
                    net.minecraft.text.Text.translatable("message.kissmod.config_required")
            ) {
                @Override
                protected void init() {
                    int screenWidth = this.width;
                    int screenHeight = this.height;
                    net.minecraft.client.gui.widget.ButtonWidget backButton =
                            net.minecraft.client.gui.widget.ButtonWidget.builder(
                                            net.minecraft.text.Text.translatable("gui.back"),
                                            (button) -> net.minecraft.client.MinecraftClient.getInstance().setScreen(parent)
                                    )
                                    .dimensions(screenWidth / 2 - 100, screenHeight / 2 + 20, 200, 20)
                                    .build();

                    this.addDrawableChild(backButton);
                }

                @Override
                public void render(net.minecraft.client.gui.DrawContext context, int mouseX, int mouseY, float delta) {
                    this.renderBackground(context, mouseX, mouseY, delta);
                    super.render(context, mouseX, mouseY, delta);

                    context.drawCenteredTextWithShadow(
                            this.textRenderer,
                            this.title,
                            this.width / 2,
                            this.height / 2 - 10,
                            0xFFFFFF
                    );
                }
            };
        }
    }
}