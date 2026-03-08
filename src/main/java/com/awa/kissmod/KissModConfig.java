package com.awa.kissmod;

import com.awa.kissmod.client.KissModConfigScreen;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Properties;

public class KissModConfig {
    private static final String CONFIG_FILE = "config/kissmod.properties";

    public static boolean rightClickEnabled = true;
    public static boolean soundEnabled = true;
    public static int particleCount = 10;
    public static boolean debugLogging = false;
    public static boolean showOwnKiss = true;
    public static boolean showOthersKiss = true;

    public static void loadConfig() {
        File configFile = new File(CONFIG_FILE);
        if (!configFile.exists()) {
            saveConfig();
            return;
        }
        try (InputStream input = new FileInputStream(configFile)) {
            Properties prop = new Properties();
            prop.load(input);
            rightClickEnabled = Boolean.parseBoolean(prop.getProperty("rightClickEnabled", "true"));
            soundEnabled = Boolean.parseBoolean(prop.getProperty("soundEnabled", "true"));
            debugLogging = Boolean.parseBoolean(prop.getProperty("debugLogging", "false"));
            showOwnKiss = Boolean.parseBoolean(prop.getProperty("showOwnKiss", "true"));
            showOthersKiss = Boolean.parseBoolean(prop.getProperty("showOthersKiss", "true"));
            try {
                particleCount = Integer.parseInt(prop.getProperty("particleCount", "10"));
            } catch (NumberFormatException e) {
                KissMod.LOGGER.warn("粒子数量无效 ({})", prop.getProperty("particleCount"), e);
                particleCount = 10;
            }
        } catch (IOException e) {
            saveConfig();
        }
    }

    public static void saveConfig() {
        File configDir = new File("config");
        if (!configDir.exists() && !configDir.mkdirs()) {
            KissMod.LOGGER.error("无法创建配置目录 `{}`", configDir.getAbsolutePath());
            return;
        }
        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            getProperties().store(output, "KissMod Configuration");
        } catch (IOException e) {
            KissMod.LOGGER.error("保存配置文件失败", e);
        }
    }

    @NotNull
    private static Properties getProperties() {
        Properties prop = new Properties();
        prop.setProperty("rightClickEnabled", String.valueOf(rightClickEnabled));
        prop.setProperty("soundEnabled",      String.valueOf(soundEnabled));
        prop.setProperty("particleCount",     String.valueOf(particleCount));
        prop.setProperty("debugLogging",      String.valueOf(debugLogging));
        prop.setProperty("showOwnKiss",       String.valueOf(showOwnKiss));
        prop.setProperty("showOthersKiss",    String.valueOf(showOthersKiss));
        return prop;
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