package com.awa.kissmod;

import com.awa.kissmod.client.KissModConfigScreen;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;

import java.io.*;
import java.util.Properties;

public class KissModConfig {
    private static final String CONFIG_FILE = "config/kissmod.properties";
    private static final String RIGHT_CLICK_KEY = "rightClickEnabled";
    private static final String SOUND_ENABLED_KEY = "soundEnabled";
    private static final String PARTICLE_COUNT_KEY = "particleCount";
    private static final String DEBUG_LOGGING_KEY = "debugLogging";

    public static boolean rightClickEnabled = true;
    public static boolean soundEnabled = true;
    public static int particleCount = 10;
    public static boolean debugLogging = false;

    public static void loadConfig() {
        File configFile = new File(CONFIG_FILE);
        if (!configFile.exists()) {
            saveConfig(rightClickEnabled, soundEnabled, particleCount, debugLogging);
            return;
        }
        try (InputStream input = new FileInputStream(configFile)) {
            Properties prop = new Properties();
            prop.load(input);
            rightClickEnabled = Boolean.parseBoolean(prop.getProperty(RIGHT_CLICK_KEY, "true"));
            soundEnabled = Boolean.parseBoolean(prop.getProperty(SOUND_ENABLED_KEY, "true"));
            debugLogging = Boolean.parseBoolean(prop.getProperty(DEBUG_LOGGING_KEY, "false"));
            try {
                particleCount = Integer.parseInt(prop.getProperty(PARTICLE_COUNT_KEY, "10"));
            } catch (NumberFormatException e) {
                KissMod.LOGGER.warn("粒子数量无效 ({})", prop.getProperty(PARTICLE_COUNT_KEY), e);
                particleCount = 10;
            }
        } catch (IOException e) {
            saveConfig(rightClickEnabled, soundEnabled, particleCount, debugLogging);
        }
    }

    public static void saveConfig(boolean rightClickState, boolean soundState, int particleCountState, boolean debugLoggingState) {
        rightClickEnabled = rightClickState;
        soundEnabled = soundState;
        particleCount = particleCountState;
        debugLogging = debugLoggingState;

        File configDir = new File("config");
        if (!configDir.exists()) {
            boolean created = configDir.mkdirs();
            if (!created) {
                KissMod.LOGGER.error("无法创建配置目录 `{}`", configDir.getAbsolutePath());
                return;
            }
        }
        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            Properties prop = new Properties();
            prop.setProperty(RIGHT_CLICK_KEY, String.valueOf(rightClickState));
            prop.setProperty(SOUND_ENABLED_KEY, String.valueOf(soundState));
            prop.setProperty(PARTICLE_COUNT_KEY, String.valueOf(particleCountState));
            prop.setProperty(DEBUG_LOGGING_KEY, String.valueOf(debugLoggingState));
            prop.store(output, "KissMod Configuration");
        } catch (IOException e) {
            KissMod.LOGGER.error("保存配置文件失败", e);
        }
    }

    public static Screen getScreen(Screen parent) {
        if (FabricLoader.getInstance().isModLoaded("cloth-config")) {
            try {
                return KissModConfigScreen.createConfigScreen(parent);
            } catch (Exception e) {
                KissMod.LOGGER.error("创建配置界面失败", e);
                return createFallbackScreen(parent);
            }
        } else {
            return createFallbackScreen(parent);
        }
    }

    private static Screen createFallbackScreen(Screen parent) {
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